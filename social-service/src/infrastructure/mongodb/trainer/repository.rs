use async_trait::async_trait;
use futures::{TryStreamExt, StreamExt};
use mongodb::bson::spec::BinarySubtype;
use mongodb::{
    bson::{doc, Binary},
    Collection,
};

use crate::{
    application::trainer::{errors::ApplicationError, traits::repository::Repository},
    domain::{shared::uuid::UUID, trainer::root::Trainer},
};

use super::entity::TrainerEntity;

#[derive(Clone)]
pub struct MongodbRepository(pub(crate) Collection<TrainerEntity>);

#[async_trait]
impl Repository for MongodbRepository {
    async fn get(&self, id: UUID) -> Result<Option<Trainer>, ApplicationError> {
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": false
        };

        Ok(self
            .0
            .find_one(query, None)
            .await
            .map_err(|err| ApplicationError::UnhandledError(Box::new(err)))?
            .map(Into::into))
    }

    async fn get_all(&self) -> Result<Vec<Trainer>, ApplicationError> {
        let query = doc! {
            "deleted": false
        };

        Ok(self
            .0
            .find(query, None)
            .await
            .map_err(|err| ApplicationError::UnhandledError(Box::new(err)))?
            .into_stream()
            .map(|result| result.unwrap().into())
            .collect()
            .await)
    }
}
