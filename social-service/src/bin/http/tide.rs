use social_service::{
    application::participant::traits::use_case::UseCase,
    domain::shared::uuid::UUID,
    infrastructure::{
        forms::participant::ParticipantForm, view_models::participant::ParticipantViewModel,
    },
};

use social_service::infrastructure::middlewares::tide::decode_id::DecodeIdMiddleware;
use tide::{log::LogMiddleware, Body, Request, Response};

extern crate async_std;
extern crate social_service;
extern crate tide;

async fn show<T: UseCase>(request: Request<T>) -> tide::Result {
    let service = request.state();
    let id: &UUID = request.ext().unwrap();

    let vm: Option<ParticipantViewModel> =
        service.get(id.to_string().as_str()).await.map(Into::into);

    match vm {
        Some(vm) => Ok(Body::from_json(&vm)?.into()),
        None => Ok(Response::builder(404).into()),
    }
}

async fn index<T: UseCase>(request: Request<T>) -> tide::Result {
    let service = request.state();
    let vms: Vec<ParticipantViewModel> = service
        .get_all()
        .await
        .into_iter()
        .map(Into::into)
        .collect();

    Ok(Body::from_json(&vms)?.into())
}

async fn create<T: UseCase>(mut request: Request<T>) -> tide::Result {
    let form: ParticipantForm = request.body_json().await?;
    let service = request.state();
    let preferences: Vec<&str> = form
        .preferences
        .iter()
        .map(|string| string.as_str())
        .collect();

    let result = service
        .create(&*form.name, &form.password, &*preferences)
        .await;

    match result {
        Ok(()) => Ok("OK".into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn server<T: UseCase + Clone + Send + Sync + 'static>(service: T) -> std::io::Result<()> {
    let mut app = tide::with_state(service);

    app.with(LogMiddleware::new());

    app.at("/participants").post(create).get(index);

    app.at("/participants/:id")
        .with(DecodeIdMiddleware::new())
        .get(show);

    println!("Starting application on port 4000");

    app.listen("0.0.0.0:4000").await.unwrap();

    Ok(())
}
