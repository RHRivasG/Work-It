use std::error::Error;

use async_trait::async_trait;
use mongodb::{
    bson::{doc, spec::BinarySubtype, Binary},
    Collection,
};
use tide::log::debug;

use crate::{
    application::transformation_request::errors::ApplicationError,
    domain::{shared::uuid::UUID, transformation_request::events::TransformationRequestEvent},
    infrastructure::publisher::handler::Handler,
};

use super::entity::RequestEntity;

pub struct RequestValidator(Collection<RequestEntity>);

impl RequestValidator {
    pub fn new(collection: Collection<RequestEntity>) -> Self {
        RequestValidator(collection)
    }

    pub async fn is_issued(&self, id: UUID) -> Result<bool, Box<dyn Error + Sync + Send>> {
        let query = doc! {
            "id": Binary { bytes: id.to_vec(), subtype: BinarySubtype::Uuid },
            "deleted": false
        };

        debug!(target: "request_validator", "Validating existance of transformation request for participant {:?}", id);

        let result = self
            .0
            .find_one(query, None)
            .await
            .map_err(Box::new)?;

        if let Some(ref request) = result {
            debug!(target: "request_validator","CONFLICT: Request found for id {:?} issued at {}", id, request.issued_at);
        }

        Ok(result.is_some())
    }
}

#[async_trait]
impl Handler<TransformationRequestEvent, ApplicationError> for RequestValidator {
    async fn handle(&self, evt: &TransformationRequestEvent) -> Result<(), ApplicationError> {
        match evt {
            TransformationRequestEvent::TransformationRequestCreated { participant_id, .. } => {
                let is_issued = self
                    .is_issued(*participant_id)
                    .await
                    .map_err(ApplicationError::UnhandledError)?;

                if is_issued {
                    return Err(ApplicationError::AlreadyIssued);
                }

                Ok(())
            }
            _ => Ok(())
        }
    }

    async fn compensate(&self, _: &TransformationRequestEvent) -> Result<(), ApplicationError> {
        Ok(())
    }
}
