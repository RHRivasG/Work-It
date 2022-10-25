use async_trait::async_trait;
use futures::TryFutureExt;

use crate::{
    application::{
        participant::errors::ApplicationError,
        transformation_request::{errors::ApplicationError as TRError, traits::use_case::UseCase},
    },
    domain::participant::events::ParticipantEvent,
    infrastructure::{publisher::handler::Handler, dependencies::transformation_request::create_service},
};

#[async_trait]
impl<T> Handler<ParticipantEvent, ApplicationError> for T
where
    T: 'static + UseCase + Sync + Send,
{
    async fn handle(&self, evt: &ParticipantEvent) -> Result<(), ApplicationError> {
        match evt {
            ParticipantEvent::ParticipantTransformationRequestIssued { id } => {
                self.create_transformation_request(*id)
                    .map_err(|err| match err {
                        TRError::AlreadyIssued => ApplicationError::RequestAlreadyIssued,
                        TRError::UnhandledError(err) => ApplicationError::UnhandledError(err),
                        _ => unreachable!(),
                    })
                    .await
            }
            _ => Ok(()),
        }
    }

    async fn compensate(&self, evt: &ParticipantEvent) -> Result<(), ApplicationError> {
        match evt {
            ParticipantEvent::ParticipantTransformationRequestIssued { id } => {
                self.delete_transformation_request(*id)
                    .map_err(|err| match err {
                        TRError::AlreadyIssued => ApplicationError::RequestAlreadyIssued,
                        TRError::UnhandledError(err) => ApplicationError::UnhandledError(err),
                        _ => unreachable!(),
                    })
                    .map_ok(|_| ())
                    .await
            }
            _ => Ok(()),
        }
    }
}

pub async fn create_requests_handler() -> impl Handler<ParticipantEvent, ApplicationError> {
    create_service().await
}
