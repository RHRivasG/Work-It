use std::error::Error;

use async_trait::async_trait;
use chrono::{Utc, DateTime};
use mongodb::{Collection, bson::{Binary, spec::BinarySubtype, doc}};
use tide::log::debug;

use crate::{domain::{shared::uuid::UUID, transformation_request::events::TransformationRequestEvent}, infrastructure::publisher::handler::Handler, application::transformation_request::errors::ApplicationError};

use super::entity::RequestEntity;

pub struct RequestHandler(Collection<RequestEntity>);

impl RequestHandler {
    pub fn new(collection: Collection<RequestEntity>) -> Self {
        RequestHandler(collection)
    }

    pub async fn create(&self, id: UUID, issued_at: DateTime<Utc>) -> Result<(), Box<dyn Error + Send + Sync>> {
        let entity = RequestEntity {
            id: Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            issued_at: issued_at.into(),
            accepted_at: None,
            rejected_at: None,
            deleted: false
        };

        debug!(target: "request_handler", "Creating transformation request: {:?}", entity);

        self.0.insert_one(entity, None).await.map_err(Box::new)?;

        Ok(())
    }

    pub async fn set_accepted_at(&self, id: UUID, accepted_at: Option<DateTime<Utc>>) -> Result<(), Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": false
        };

        debug!(target: "request_handler", "Accepting transformation request: {:?}", id);

        let update = doc! {
            "$set": {
                "accepted_at": accepted_at
            }
        };

        self.0.update_one(query, update, None).await.map_err(Box::new)?;

        Ok(())
    }

    pub async fn set_rejected_at(&self, id: UUID, rejected_at: Option<DateTime<Utc>>) -> Result<(), Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": false
        };

        debug!(target: "request_handler", "Rejecting transformation request: {:?}", id);

        let update = doc! {
            "$set": {
                "rejected_at": rejected_at
            }
        };

        self.0.update_one(query, update, None).await.map_err(Box::new)?;

        Ok(())
    }

    pub async fn delete(&self, id: UUID) -> Result<(), Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": false
        };
        let update = doc! {
            "$set": {
                "deleted": true
            }
        };

        debug!(target: "request_handler", "Deleting transformation request: {:?}", id);

        self.0.update_one(query, update, None).await.map_err(Box::new)?;

        Ok(())
    }

    pub async fn undelete(&self, id: UUID) -> Result<(), Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": true
        };
        let update = doc! {
            "$set": {
                "deleted": false
            }
        };

        debug!(target: "request_handler", "Undoing deletion of transformation request: {:?}", id);

        self.0.update_one(query, update, None).await.map_err(Box::new)?;

        Ok(())
    }
}

#[async_trait]
impl Handler<TransformationRequestEvent, ApplicationError> for RequestHandler {
    async fn handle(&self, evt: &TransformationRequestEvent) -> Result<(), ApplicationError> {
        match evt {
            TransformationRequestEvent::TransformationRequestCreated { participant_id, issued_at } => {
                self.create(*participant_id, *issued_at).await.map_err(ApplicationError::UnhandledError)?;
                Ok(())
            }
            TransformationRequestEvent::TransformationRequestApproved { participant_id, accepted_at } => {
                self.set_accepted_at(*participant_id, Some(*accepted_at)).await.map_err(ApplicationError::UnhandledError)?;
                Ok(())
            }
            TransformationRequestEvent::TransformationRequestRejected { participant_id, rejected_at } => {
                self.set_rejected_at(*participant_id, Some(*rejected_at)).await.map_err(ApplicationError::UnhandledError)?;
                Ok(())
            },
            TransformationRequestEvent::TransformationRequestDeleted { participant_id } => {
                self.delete(*participant_id).await.map_err(ApplicationError::UnhandledError)
            }
        }
    }
    async fn compensate(&self, evt: &TransformationRequestEvent) -> Result<(), ApplicationError> {
        match evt {
            TransformationRequestEvent::TransformationRequestCreated { participant_id, .. } => {
                self.delete(*participant_id).await.map_err(ApplicationError::UnhandledError)?;
                Ok(())
            }
            TransformationRequestEvent::TransformationRequestApproved { participant_id, .. } => {
                self.set_accepted_at(*participant_id, None).await.map_err(ApplicationError::UnhandledError)?;
                Ok(())
            }
            TransformationRequestEvent::TransformationRequestRejected { participant_id, .. } => {
                self.set_rejected_at(*participant_id, None).await.map_err(ApplicationError::UnhandledError)?;
                Ok(())
            }
            TransformationRequestEvent::TransformationRequestDeleted { participant_id } => {
                self.undelete(*participant_id).await.map_err(ApplicationError::UnhandledError)
            }
        }
    }
}
