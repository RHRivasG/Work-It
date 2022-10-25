use std::error::Error;

use async_trait::async_trait;
use futures::TryFutureExt;
use mongodb::{Collection, bson::{spec::BinarySubtype, Binary, doc}};
use tide::log::debug;

use crate::{
    application::participant::errors::ApplicationError,
    domain::{participant::events::ParticipantEvent, shared::uuid::UUID}, infrastructure::publisher::handler::Handler,
};

use super::entity::ParticipantEntity;

pub struct ParticipantHandler(Collection<ParticipantEntity>);

impl ParticipantHandler {
    pub(crate) fn new(collection: Collection<ParticipantEntity>) -> Self {
        ParticipantHandler(collection)
    }

    async fn create(
        &self,
        id: UUID,
        name: String,
        password: String,
        preferences: Vec<String>,
    ) -> Result<(), Box<dyn Error + Sync + Send>> {
        let entity = ParticipantEntity {
            id: Binary {
                subtype: BinarySubtype::Uuid,
                bytes: id.to_vec(),
            },
            name,
            password,
            preferences,
            deleted: false,
        };

        debug!(target: "participant_handler", "Inserting participant {:?}", entity);

        self.0.insert_one(entity, None).await.map_err(Box::new)?;

        Ok(())
    }

    async fn update_preferences(
        &self,
        id: UUID,
        added_preferences: Vec<String>,
        removed_preferences: Vec<String>,
    ) -> Result<(), Box<dyn Error + Send + Sync>> {
        let query = doc! { "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": false };

        debug!(target: "participant_handler", "Adding preferences {:?} to id {:?}", &*added_preferences, id);

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

        debug!(target: "participant_handler", "Removing preferences {:?} from id {:?}", &*removed_preferences, id);

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

    async fn update_password(&self, id: UUID, password: String) -> Result<(), Box<dyn Error + Send + Sync>> {
        debug!(target: "participant_handler", "Set password {:?} to id {:?}", &*password, id);

        let update = doc! {
            "$set": {
                "password": &*password
            }
        };

        let query = doc! { "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": false };

        self.0
            .update_one(query, update, None)
            .await
            .map_err(Box::new)?;

        Ok(())
    }

    async fn update_name(&self, id: UUID, name: String) -> Result<(), Box<dyn Error + Send + Sync>> {
        debug!(target: "participant_handler", "Set name {:?} to id {:?}", &*name, id);

        let update = doc! {
            "$set": {
                "name": &*name
            }
        };

        let query = doc! { "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": false };

        self.0
            .update_one(query, update, None)
            .await
            .map_err(Box::new)?;

        Ok(())
    }

    async fn delete(&self, id: UUID) -> Result<(), Box<dyn Error + Send + Sync>> {
        debug!(target: "participant_handler", "Deleting with id {:?}", id);

        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": false
        };
        let update = doc! {
            "$set": {
                "deleted": true
            }
        };

        self.0
            .update_one(query, update, None)
            .await
            .map_err(Box::new)?;

        Ok(())
    }

    async fn undelete(&self, id: UUID) -> Result<(), Box<dyn Error + Send + Sync>> {
        debug!(target: "participant_handler", "Undoing delete with id {:?}", id);

        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": true
        };
        let update = doc! {
            "$set": {
                "deleted": false
            }
        };

        self.0
            .update_one(query, update, None)
            .await
            .map_err(Box::new)?;

        Ok(())
    }
}

#[async_trait]
impl Handler<ParticipantEvent, ApplicationError> for ParticipantHandler {
    async fn handle(&self, evt: &ParticipantEvent) -> Result<(), ApplicationError> {
        let repository = &self;
        match evt {
            ParticipantEvent::ParticipantCreated {
                id,
                name,
                password,
                preferences,
            } => {
                repository
                    .create(*id, name.clone(), password.clone(), preferences.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantNameUpdated { id, name, .. } => {
                repository
                    .update_name(*id, name.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantPasswordUpdated { id, password, .. } => {
                repository
                    .update_password(*id, password.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantPreferencesUpdated {
                id,
                added_preferences,
                removed_preferences,
            } => {
                repository
                    .update_preferences(*id, added_preferences.clone(), removed_preferences.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantDeleted { id } => {
                repository
                    .delete(*id)
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantTransformationRequestIssued { id: _ } => (),
        };
        Ok(())
    }

    async fn compensate(&self, evt: &ParticipantEvent) -> Result<(), ApplicationError> {
        match evt {
            ParticipantEvent::ParticipantCreated { id, .. } => {
                self.delete(*id)
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantPreferencesUpdated {
                id,
                added_preferences,
                removed_preferences,
                ..
            } => {
                self.update_preferences(*id, removed_preferences.clone(), added_preferences.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantNameUpdated {
                id, previous_name, ..
            } => {
                self.update_name(*id, previous_name.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantPasswordUpdated {
                id,
                previous_password,
                ..
            } => {
                self.update_password(*id, previous_password.clone())
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantDeleted { id } => {
                self.undelete(*id)
                    .map_err(ApplicationError::UnhandledError)
                    .await?
            }
            ParticipantEvent::ParticipantTransformationRequestIssued { .. } => {}
        };

        Ok(())
    }
}
