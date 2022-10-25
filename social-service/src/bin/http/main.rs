pub mod tide;

use social_service::infrastructure::dependencies::{
    participant::{create_service as create_participant_service, UseCase as ParticipantUseCase},
    trainer::{create_service as create_trainer_service, UseCase as TrainerUseCase},
    transformation_request::{
        create_service as create_transformation_request_service,
        UseCase as TransformationRequestUseCase,
    },
    ApplicationState,
};

#[cfg(feature = "tide")]
use crate::tide::server;

#[async_std::main]
pub async fn main() -> std::io::Result<()> {
    let state: ApplicationState<ParticipantUseCase, TransformationRequestUseCase, TrainerUseCase> =
        ApplicationState::new(
            create_participant_service().await,
            create_transformation_request_service().await,
            create_trainer_service().await,
        );

    server(state).await?;

    Ok(())
}
