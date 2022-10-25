use async_trait::async_trait;

use crate::{
    application::{
        participant::{
            errors::ApplicationError as ParticipantApplicationError, traits::use_case::UseCase,
        },
        transformation_request::errors::ApplicationError,
    },
    domain::transformation_request::events::TransformationRequestEvent,
    infrastructure::publisher::handler::Handler,
};

#[async_trait]
impl<T> Handler<TransformationRequestEvent, ApplicationError> for T
where
    T: UseCase + Sync + Send,
{
    async fn handle(&self, evt: &TransformationRequestEvent) -> Result<(), ApplicationError> {
        match evt {
            TransformationRequestEvent::TransformationRequestApproved {
                participant_id, ..
            } => {
                self.delete(*participant_id)
                    .await
                    .map_err(|err| match err {
                        ParticipantApplicationError::UnhandledError(e) => {
                            ApplicationError::UnhandledError(e)
                        }
                        _ => unreachable!(),
                    })?;
                Ok(())
            }
            _ => Ok(()),
        }
    }
    async fn compensate(&self, _: &TransformationRequestEvent) -> Result<(), ApplicationError> {
        Ok(())
    }
}
