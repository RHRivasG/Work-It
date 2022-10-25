use ::tide::{Request, Response};
use futures::TryFutureExt;
use social_service::{
    application::trainer::traits::use_case::UseCase,
    domain::shared::uuid::UUID,
    infrastructure::{
        dependencies::ApplicationState,
        forms::trainer::{password_update::TrainerPasswordUpdateForm, update::TrainerUpdateForm},
        view_models::trainer::TrainerViewModel,
    },
};

pub async fn update<P, TR, T>(mut request: Request<ApplicationState<P, TR, T>>) -> tide::Result
where
    T: UseCase,
{
    let TrainerUpdateForm { name, preferences } = request.body_json().await?;
    let id: &UUID = request.ext().unwrap();
    let service = request.state().trainer_service();
    let preferences: Vec<&str> = preferences.iter().map(AsRef::as_ref).collect::<Vec<&str>>();

    service
        .update(*id, name.as_str(), preferences.as_slice())
        .map_err(tide::Error::from)
        .await?;

    Ok("OK".into())
}

pub async fn update_password<P, TR, T>(
    mut request: Request<ApplicationState<P, TR, T>>,
) -> tide::Result
where
    T: UseCase,
{
    let TrainerPasswordUpdateForm { password } = request.body_json().await?;
    let id: &UUID = request.ext().unwrap();
    let service = request.state().trainer_service();

    service
        .update_password(*id, password.as_str())
        .map_err(tide::Error::from)
        .await?;

    Ok("OK".into())
}

pub async fn delete<P, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result
where
    T: UseCase,
{
    let id: &UUID = request.ext().unwrap();
    let service = request.state().trainer_service();

    let option = service.delete(*id).map_err(tide::Error::from).await?;

    match option {
        None => Ok(Response::builder(404).into()),
        Some(_) => Ok(Response::builder(204).into()),
    }
}

pub async fn get_all<P, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result
where
    T: UseCase,
{
    let service = request.state().trainer_service();

    let trainers: Vec<TrainerViewModel> = service
        .get_all()
        .map_err(tide::Error::from)
        .await
        ?.into_iter()
        .map(TrainerViewModel::from)
        .collect();

    Ok(tide::Body::from_json(&trainers)?.into())
}

pub async fn get<P, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result
where
    T: UseCase,
{
    let service = request.state().trainer_service();
    let id: &UUID = request.ext().unwrap();

    let option = service
        .get(*id)
        .map_err(tide::Error::from)
        .await?
        .map(TrainerViewModel::from);

    match option {
        None => Ok(Response::builder(404).into()),
        Some(vm) => Ok(tide::Body::from_json(&vm)?.into()),
    }
}
