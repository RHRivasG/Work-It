use std::{error::Error, fmt::Display};

use async_trait::async_trait;
use futures::{TryFutureExt};
use mongodb::{
    bson::{doc, spec::BinarySubtype, Binary, Bson, Document},
    Collection,
};
use serde::Serialize;
use tide::log::debug;

use crate::{
    application::trainer::errors::ApplicationError,
    domain::{shared::uuid::UUID, trainer::events::TrainerEvent},
    infrastructure::publisher::handler::Handler,
};

use super::entity::TrainerEntity;

type Result<T> = std::result::Result<T, Box<dyn Error + Sync + Send + 'static>>;

#[derive(Clone)]
pub struct MongodbHandler(pub(crate) Collection<TrainerEntity>);

impl MongodbHandler {
    async fn create(
        &self,
        id: UUID,
        name: String,
        password: String,
        preferences: Vec<String>,
    ) -> Result<()> {
        let entity = TrainerEntity::new(id, name, password, preferences);

        debug!(target: "trainer_handler", "Creating trainer {:?}", entity);

        self.0.insert_one(entity, None).await.map_err(Box::new)?;

        Ok(())
    }
    async fn update_property<T>(
        &self,
        property: &str,
        id: UUID,
        value: T,
        deleted: bool,
    ) -> Result<()>
    where
        T: Serialize + Display,
        Bson: From<T>,
    {
        debug!(target: "trainer_handler", "Updating property {} on trainer {:?} with value {}", property, id, &value);

        let update = Document::from_iter([(property.to_string(), Bson::from(value))]);

        let id = Binary {
            subtype: BinarySubtype::Uuid,
            bytes: id.to_vec(),
        };

        let query = Document::from_iter([
            ("id".to_string(), Bson::Binary(id)),
            ("deleted".to_string(), Bson::Boolean(deleted)),
        ]);

        self.0
            .update_one(query, update, None)
            .await
            .map_err(Box::new)?;

        Ok(())
    }
    async fn update_preferences(
        &self,
        id: UUID,
        added_preferences: Vec<String>,
        removed_preferences: Vec<String>,
    ) -> Result<()> {
        let query = doc! { "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": false };

        debug!(target: "trainer_handler", "Adding preferences {:?} to id {:?}", &*added_preferences, id);

        self.0
            .update_one(
                query.clone(),
                doc! {
                    "$push": {
                        "preferences": {
                            "$each": added_preferences
                        }
                    }
                },
                None,
            )
            .await
            .map_err(Box::new)?;

        debug!(target: "trainer_handler", "Removing preferences {:?} from id {:?}", &*removed_preferences, id);

        self.0
            .update_one(
                query,
                doc! {
                    "$pullAll": {
                        "preferences": removed_preferences
                    }
                },
                None,
            )
            .await
            .map_err(Box::new)?;

        Ok(())
    }
    async fn delete(&self, id: UUID) -> Result<()> {
        debug!(target: "trainer_handler", "Deleting trainer {:?}", id);

        self.update_property("deleted", id, true, false).await
    }
    async fn undelete(&self, id: UUID) -> Result<()> {
        debug!(target: "trainer_handler", "Undoing deletion of trainer {:?}", id);

        self.update_property("deleted", id, false, true).await
    }
}

#[async_trait]
impl Handler<TrainerEvent, ApplicationError> for MongodbHandler {
    async fn handle(&self, evt: &TrainerEvent) -> std::result::Result<(), ApplicationError> {
        match evt {
            TrainerEvent::TrainerCreated {
                id,
                name,
                password,
                preferences,
            } => Ok(self
                .create(*id, name.clone(), password.clone(), preferences.clone())
                .await
                .map_err(ApplicationError::UnhandledError)?),
            TrainerEvent::TrainerNameUpdated { id, name, .. } => Ok(self
                .update_property("name", *id, name.clone(), false)
                .await
                .map_err(ApplicationError::UnhandledError)?),
            TrainerEvent::TrainerPasswordUpdated { id, password, .. } => Ok(self
                .update_property("password", *id, password.clone(), false)
                .await
                .map_err(ApplicationError::UnhandledError)?),
            TrainerEvent::TrainerPreferencesUpdated {
                id,
                added_preferences,
                removed_preferences,
                ..
            } => Ok(self
                .update_preferences(*id, added_preferences.clone(), removed_preferences.clone())
                .await
                .map_err(ApplicationError::UnhandledError)?),
            TrainerEvent::TrainerDeleted { id } => Ok(self
                .delete(*id)
                .await
                .map_err(ApplicationError::UnhandledError)?),
        }
    }
    async fn compensate(&self, evt: &TrainerEvent) -> std::result::Result<(), ApplicationError> {
        match evt {
            TrainerEvent::TrainerCreated { id, .. } => Ok(self
                .delete(*id)
                .map_err(ApplicationError::UnhandledError)
                .await?),
            TrainerEvent::TrainerNameUpdated {
                id, previous_name, ..
            } => Ok(self
                .update_property("name", *id, previous_name.clone(), false)
                .map_err(ApplicationError::UnhandledError)
                .await?),
            TrainerEvent::TrainerPasswordUpdated {
                id,
                previous_password,
                ..
            } => Ok(self
                .update_property("password", *id, previous_password.clone(), false)
                .map_err(ApplicationError::UnhandledError)
                .await?),
            TrainerEvent::TrainerPreferencesUpdated {
                id,
                added_preferences,
                removed_preferences,
            } => Ok(self
                .update_preferences(*id, removed_preferences.clone(), added_preferences.clone())
                .map_err(ApplicationError::UnhandledError)
                .await?),
            TrainerEvent::TrainerDeleted { id } => Ok(self
                .undelete(*id)
                .map_err(ApplicationError::UnhandledError)
                .await?),
        }
    }
}
