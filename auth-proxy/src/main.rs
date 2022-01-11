mod config;
mod store;
use warp_reverse_proxy::Method;
use warp::method;
use crate::{
    config::get_config,
    store::{redis::RedisStore, TokenStore},
};
use hmac::{Hmac, Mac};
use rand::{distributions::Alphanumeric, thread_rng, Rng};
use sha2::Sha256;
use std::{net::SocketAddr, sync::Arc};
use warp::{
    cookie::optional,
    delete,
    hyper::{body::Bytes, HeaderMap, Response},
    path::{full, FullPath},
    serve, Filter, Rejection,
};
use warp_reverse_proxy::{extract_request_data_filter, proxy_to_and_forward_response};

type Encryption = Hmac<Sha256>;

fn configure_auth_token_storage(
    store: Arc<dyn TokenStore + Sync + Send>,
    proxy: impl Filter<Error = Rejection, Extract = (Response<Bytes>,)> + Clone,
) -> impl Filter<Error = Rejection, Extract = (Response<Bytes>,)> + Clone {
    proxy
        .and(full())
        .and(method())
        .map(move |resp, uri: FullPath, method| (store.clone(), uri.as_str().contains("login"), resp, method))
        .untuple_one()
        .and(optional("work-it-session"))
        .then(
            |store: Arc<dyn TokenStore + Send + Sync>,
            is_login,
            response: Response<Bytes>,
            req_method: Method,
            key: Option<String>| async move {
                if req_method == Method::GET {
                    return response
                }

                if is_login {
                    if let Some(session_key) = key {
                        store
                            .remove(session_key)
                            .await
                            .expect("Could not delete session key")
                    }
                }

                if is_login && response.status().is_success() {
                    let token = String::from_utf8(response.body().to_vec()).unwrap();

                    let rand_key: String = thread_rng()
                        .sample_iter(&Alphanumeric)
                        .take(64)
                        .map(char::from)
                        .collect();

                    let mut encryption = Encryption::new_from_slice(b"Secret key")
                        .expect("Encryption creation failed");

                    encryption.update(rand_key.as_bytes());

                    let cookie_session_key =
                        hex::encode(encryption.finalize().into_bytes().to_vec());

                    store
                        .set(cookie_session_key.clone(), token)
                        .await
                        .expect("Insertion in redis failed");

                    let mut proxy_response = Response::builder()
                        .header(
                            "Set-Cookie",
                            format!(
                                "work-it-session={}; Path=/api; HttpOnly",
                                cookie_session_key
                            ),
                        )
                        .body(Bytes::from_static(b"Authentication completed"))
                        .unwrap();

                    response.headers().iter().for_each(|(k, v)| {
                        proxy_response.headers_mut().append(k, v.clone());
                    });

                    proxy_response.headers_mut().remove("content-length");

                    proxy_response
                } else {
                    response
                }
            },
        )
}

fn configure_token(
    store: Arc<dyn TokenStore + Sync + Send>,
    proxy: String,
    base: String,
    validate_auth: bool,
) -> impl Filter<Error = Rejection, Extract = (Response<Bytes>,)> + Clone {
    let base_path = base.clone();
    let path = base_path
        .split("/")
        .skip_while(|e| e.is_empty())
        .map(|path| warp::path(path.to_string().into_boxed_str()).boxed())
        .reduce(|a, b| a.and(b).boxed())
        .expect("Empty base path received");

    path.and(full())
        .map(move |uri: FullPath| {
            (
                store.clone(),
                uri.as_str().contains("login"),
                base.to_string(),
                proxy.clone(),
            )
        })
        .untuple_one()
        .and(optional("work-it-session"))
        .and(extract_request_data_filter())
        .then(
            move |store: Arc<dyn TokenStore + Sync + Send>,
                  is_login: bool,
                  base,
                  proxy,
                  session_key: Option<String>,
                  uri,
                  params,
                  method,
                  mut headers: HeaderMap,
                  body| async move {
                if !validate_auth || !is_login {
                    if let Some(key) = session_key {
                        let token = store.get(key).await;
                        if let Ok(token) = token {
                            headers.append(
                                "Authorization",
                                format!("Bearer {}", token).parse().unwrap(),
                            );
                        }
                    }
                }
                (proxy, base, uri, params, method, headers, body)
            },
        )
        .untuple_one()
        .and_then(proxy_to_and_forward_response)
}

fn logout(
    store: Arc<dyn TokenStore + Sync + Send>,
) -> impl Filter<Error = Rejection, Extract = (Response<Bytes>,)> + Clone {
    warp::path!("api" / "logout")
        .and(optional("work-it-session"))
        .and(delete())
        .map(move |cookie: Option<String>| (store.clone(), cookie))
        .untuple_one()
        .then(
            |store: Arc<dyn TokenStore + Sync + Send>, cookie| async move {
                if let Some(key) = cookie {
                    store
                        .remove(key)
                        .await
                        .expect("Failed deleting session key");

                    Response::builder()
                        .header(
                            "Set-Cookie",
                            "work-it-session=deleted; Path=/api; HttpOnly; Max-Age=0",
                        )
                        .body(Bytes::from_static(b"Logout successful"))
                        .expect("Failed building response")
                } else {
                    Response::builder()
                        .status(405)
                        .body(Bytes::from_static(b"Must be logged in first"))
                        .expect("Failed building response")
                }
            },
        )
}

#[tokio::main]
async fn main() {
    let server_config = get_config();
    let addr = SocketAddr::from(([0, 0, 0, 0], 3000));
    let store: Arc<dyn TokenStore + Send + Sync> = Arc::new(
        RedisStore::connect()
            .await
            .expect("Connection to redis failed"),
    );
    let proxy_app = server_config
        .into_iter()
        .map(|(base, proxy, authority)| {
            if authority {
                configure_auth_token_storage(
                    store.clone(),
                    configure_token(store.clone(), proxy, base, true),
                )
                .boxed()
            } else {
                configure_token(store.clone(), proxy, base, false).boxed()
            }
        })
        .reduce(|a, b| a.or(b).unify().boxed())
        .expect("Servers not defined in configuration file")
        .or(logout(store))
        .unify();
    println!("Starting proxy on port 3000");

    serve(proxy_app).run(addr).await;
}
