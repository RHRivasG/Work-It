use std::error::Error;

use async_trait::async_trait;
use mongodb::{Collection, bson::{doc, Binary, spec::BinarySubtype}};
use tide::log::debug;

use crate::{domain::{shared::uuid::UUID, participant::events::ParticipantEvent}, application::participant::errors::ApplicationError, infrastructure::publisher::handler::Handler};

use super::entity::ParticipantEntity;

pub struct ParticipantValidator(Collection<ParticipantEntity>);

impl ParticipantValidator {
    pub(crate) fn new(collection: Collection<ParticipantEntity>) -> Self {
        ParticipantValidator(collection)
    }

    async fn id_exists(&self, id: UUID) -> Result<bool, Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "id": Binary { bytes: id.to_vec(), subtype: BinarySubtype::Uuid },
            "deleted": false
        };
        
        debug!("Validating uniqueness for ID {:?}", id);

        let is_taken = self.0.find_one(query, None).await.map_err(Box::new)?.is_some();

        if is_taken {
            debug!("[CONFLICT] Provided ID is already taken");
        }
        
        Ok(is_taken)
    }

    async fn name_exists(&self, id: UUID, name: &str) -> Result<bool, Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "name": name,
            "id": {
                "$ne": Binary { bytes: id.to_vec(), subtype: BinarySubtype::Uuid }
            },
            "deleted": false
        };

        debug!("Validating uniqueness for name {:?}", name);

        let is_taken = self.0.find_one(query, None).await.map_err(Box::new)?.is_some();

        if is_taken {
            debug!("[CONFLICT] Provided name is already taken");
        }
        
        Ok(is_taken)
    }
}

#[async_trait]
impl Handler<ParticipantEvent, ApplicationError> for ParticipantValidator {
    async fn handle(&self, event: &ParticipantEvent) -> Result<(), ApplicationError> {
        match event {
            ParticipantEvent::ParticipantCreated { id, name, .. } => {
                let id_exists = self.id_exists(*id).await.map_err(ApplicationError::UnhandledError)?;
                let name_exists = self.name_exists(*id, name.as_ref()).await.map_err(ApplicationError::UnhandledError)?;

                if id_exists {
                    return Err(ApplicationError::IdTaken)
                }

                if name_exists {
                    return Err(ApplicationError::NameTaken)
                }

                Ok(())
            },
            ParticipantEvent::ParticipantNameUpdated { id, name, .. } => {
                let name_exists = self.name_exists(*id, name.as_ref()).await.map_err(ApplicationError::UnhandledError)?;

                if name_exists {
                    return Err(ApplicationError::NameTaken)
                }

                Ok(())
            }
            _ => Ok(())
        }
    }
    async fn compensate(&self, _: &ParticipantEvent) -> Result<(), ApplicationError> {
        Ok(())
    }
}
