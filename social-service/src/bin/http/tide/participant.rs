use ::tide::{Body, Request, Response};
use social_service::{
    application::participant::traits::use_case::UseCase,
    domain::shared::uuid::UUID,
    infrastructure::{
        forms::{participant::ParticipantForm, participant::update::ParticipantUpdateForm, participant::password_update::ParticipantPasswordUpdateForm},
        view_models::participant::ParticipantViewModel, dependencies::ApplicationState,
    },
};

pub async fn delete<P: UseCase, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let service = request.state().participant_service();
    let id: &UUID = request.ext().unwrap();

    let result = service.delete(*id).await;

    match result {
        Ok(Some(())) => Ok(Response::builder(204).into()),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn update_password<P: UseCase, TR, T>(mut request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let form: ParticipantPasswordUpdateForm  = request.body_json().await?;
    let service = request.state().participant_service();
    let id: &UUID = request.ext().unwrap();

    let result = service.update_password(*id, form.password.as_ref()).await;

    match result {
        Ok(Some(())) => Ok(Response::builder(204).into()),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn update<P: UseCase, TR, T>(mut request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let form: ParticipantUpdateForm = request.body_json().await?;
    let service = request.state().participant_service();
    let id: &UUID = request.ext().unwrap();
    let preferences: Vec<&str> = form
        .preferences
        .iter()
        .map(|string| string.as_str())
        .collect();

    let result = service
        .update(*id, form.name.as_ref(), preferences.as_ref())
        .await;

    match result {
        Ok(Some(())) => Ok(Response::builder(204).into()),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn show<P: UseCase, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let service = request.state().participant_service();
    let id: &UUID = request.ext().unwrap();
    let participant = service.get(*id).await;

    match participant {
        Ok(Some(dto)) => Ok(ParticipantViewModel::from(dto).try_into()?),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn preferences<P: UseCase, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let service = request.state().participant_service();
    let participants = service.get_all().await;

    match participants {
        Ok(participants) => Ok(Body::from_json(
            &participants
                .iter()
                .flat_map(|p| &*p.preferences)
                .cloned()
                .collect::<Vec<Box<str>>>(),
        )?
        .into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn index<P: UseCase, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let service = request.state().participant_service();
    let vms = service.get_all().await;

    match vms {
        Ok(vms) => Ok(Body::from_json(
            &vms.into_iter()
                .map(ParticipantViewModel::from)
                .collect::<Vec<ParticipantViewModel>>(),
        )?
        .into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn create<P: UseCase, TR, T>(mut request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let form: ParticipantForm = request.body_json().await?;
    let service = request.state().participant_service();
    let preferences: Vec<&str> = form
        .preferences
        .iter()
        .map(|string| string.as_str())
        .collect();

    let result = service
        .create(&*form.name, &form.password, &*preferences)
        .await;

    match result {
        Ok(()) => Ok(Response::builder(201).into()),
        Err(err) => Ok(err.into()),
    }
}

pub async fn request_transformation<P: UseCase, TR, T>(request: Request<ApplicationState<P, TR, T>>) -> tide::Result {
    let service = request.state().participant_service();
    let id: &UUID = request.ext().unwrap();

    let result = service.request_transformation(*id).await;

    match result {
        Ok(Some(())) => Ok(Response::builder(201).into()),
        Ok(None) => Ok(Response::builder(404).into()),
        Err(err) => Ok(err.into())
    }
}
