pub mod participant;
pub mod transformation_request;
pub mod trainer;

use social_service::application::participant::traits::use_case::UseCase as ParticipantUseCase;
use social_service::application::transformation_request::traits::use_case::UseCase as TransformationRequestUseCase;
use social_service::application::trainer::traits::use_case::UseCase as TrainerUseCase;
use social_service::infrastructure::dependencies::ApplicationState;
use social_service::infrastructure::middlewares::tide::decode_id::DecodeIdMiddleware;
use tide::log::LogMiddleware;

extern crate async_std;
extern crate social_service;
extern crate tide;

pub async fn server<
    P: 'static + ParticipantUseCase + Clone + Send + Sync,
    TR: 'static + TransformationRequestUseCase + Clone + Send + Sync,
    T: 'static + TrainerUseCase + Clone + Send + Sync,
>(
    service: ApplicationState<P, TR, T>,
) -> std::io::Result<()> {
    env_logger::init();

    let mut app = tide::with_state(service);

    app.with(LogMiddleware::new());

    app.at("/participants")
            .post(participant::create)
            .get(participant::index);
    app.at("/participants/:id")
        .with(DecodeIdMiddleware::new())
            .get(participant::show)
            .put(participant::update)
            .delete(participant::delete)
            .at("/password")
                .put(participant::update_password);

    app.at("/participants/:id/request")
        .with(DecodeIdMiddleware::new())
            .post(participant::request_transformation)
                .at("/accept")
                    .post(transformation_request::accept)
                .at("/reject")
                    .post(transformation_request::reject);

    app.at("/participants/preferences")
        .get(participant::preferences);
    
    app.at("/trainers")
        .get(trainer::get_all);

    app.at("/trainers/:id")
        .with(DecodeIdMiddleware::new())
            .get(trainer::get)
            .put(trainer::update)
            .delete(trainer::delete)
            .at("/password")
                .put(trainer::update_password);

    app.listen("0.0.0.0:4000").await.unwrap();

    Ok(())
}
