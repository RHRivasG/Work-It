use std::error::Error;

use async_trait::async_trait;
use futures::TryFutureExt;
use mongodb::{Collection, bson::{doc, Binary, spec::BinarySubtype}};

use crate::{infrastructure::publisher::handler::Handler, domain::{trainer::events::TrainerEvent, shared::uuid::UUID}, application::trainer::errors::ApplicationError};

use super::entity::TrainerEntity;

#[derive(Clone)]
pub struct MongodbValidator(pub(crate) Collection<TrainerEntity>);

impl MongodbValidator {
    pub async fn is_id_taken(&self, id: UUID) -> Result<bool, Box<dyn Error + Sync + Send + 'static>>{
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": false
        };

        Ok(self.0.find_one(query, None).await.map_err(Box::new)?.is_some())
    }

    pub async fn is_name_taken(&self, name: String) -> Result<bool, Box<dyn Error + Sync + Send + 'static>>{
        let query = doc! {
            "name": name,
            "deleted": false
        };

        Ok(self.0.find_one(query, None).await.map_err(Box::new)?.is_some())
    }
}

#[async_trait]
impl Handler<TrainerEvent, ApplicationError> for MongodbValidator {
    async fn handle(&self, evt: &TrainerEvent) -> Result<(), ApplicationError> {
        match evt {
            TrainerEvent::TrainerCreated { id, name, .. } => {
                if self.is_id_taken(*id).map_err(ApplicationError::UnhandledError).await? {
                    return Err(ApplicationError::IdTaken);
                }

                if self.is_name_taken(name.clone()).map_err(ApplicationError::UnhandledError).await? {
                    return Err(ApplicationError::NameTaken)
                }

                Ok(())
            }
            TrainerEvent::TrainerNameUpdated { name, .. } => {
                if self.is_name_taken(name.clone()).map_err(ApplicationError::UnhandledError).await? {
                    return Err(ApplicationError::NameTaken)
                }

                Ok(())
            }
            _  => Ok(()),
        }
    }

    async fn compensate(&self, _: &TrainerEvent) -> Result<(), ApplicationError> {
        Ok(())
    }
}
