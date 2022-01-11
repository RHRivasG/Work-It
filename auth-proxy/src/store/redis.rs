use std::pin::Pin;

use futures::FutureExt;
use redis::{cmd, Client, RedisError};

use super::TokenStore;

pub struct RedisStore {
    client: Client,
}

impl RedisStore {
    pub async fn connect() -> Result<Self, RedisError> {
        let redis_host = std::env::var("DB_HOST").expect("Missing redis host");
        let client = Client::open(format!("redis://{}:6379", redis_host))?;

        Ok(RedisStore { client })
    }
}

impl TokenStore for RedisStore {
    fn set<'a>(
        &'a self,
        key: String,
        token: String,
    ) -> Pin<Box<dyn std::future::Future<Output = Result<(), RedisError>> + Send>> {
        let client = self.client.clone();

        async move {
            let ref mut con = client.get_async_connection().await?;
            cmd("SET").arg(&[key, token]).query_async(con).await?;

            Ok(())
        }
        .boxed()
    }

    fn get<'a>(
        &'a self,
        key: String,
    ) -> Pin<Box<dyn std::future::Future<Output = Result<String, RedisError>> + Send>> {
        let client = self.client.clone();

        async move {
            let ref mut con = client.get_async_connection().await?;
            let token = cmd("GET").arg(&key).query_async(con).await?;

            Ok(token)
        }
        .boxed()
    }

    fn remove<'a>(
        &'a self,
        key: String,
    ) -> Pin<Box<dyn futures::Future<Output = Result<(), RedisError>> + Send>> {
        let client = self.client.clone();

        async move {
            let ref mut con = client.get_async_connection().await?;
            cmd("DEL").arg(&key).query_async(con).await?;

            Ok(())
        }
        .boxed()
    }
}
