use social_service::{application::transformation_request::traits::use_case::UseCase, infrastructure::dependencies::ApplicationState, domain::shared::uuid::UUID};
use ::tide::{Request, Response};

pub async fn accept<P, TR: UseCase, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let id: &UUID = request.ext().unwrap();
    let service = request.state().transformation_request_service();
    let result = service.accept_transformation_request(*id).await;

    match result {
        Ok(Some(())) => Ok(Response::builder(200).into()),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into())
    }
}

pub async fn reject<P, TR: UseCase, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let id: &UUID = request.ext().unwrap();
    let service = request.state().transformation_request_service();
    let result = service.reject_transformation_request(*id).await;

    match result {
        Ok(Some(())) => Ok(Response::builder(200).into()),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into())
    }
}
