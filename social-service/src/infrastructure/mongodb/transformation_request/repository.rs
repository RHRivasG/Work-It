use std::error::Error;

use async_trait::async_trait;
use mongodb::{
    bson::{doc, spec::BinarySubtype, Binary},
    Collection,
};
use tide::log::debug;

use crate::{
    application::transformation_request::{
        errors::ApplicationError, traits::repository::Repository,
    },
    domain::{shared::uuid::UUID, transformation_request::root::Request},
};

use super::entity::RequestEntity;

#[derive(Clone)]
pub struct RequestRepository(Collection<RequestEntity>);

impl RequestRepository {
    pub fn new(collection: Collection<RequestEntity>) -> Self {
        RequestRepository(collection)
    }

    pub async fn find(&self, id: UUID) -> Result<Option<Request>, Box<dyn Error + Send + Sync>> {
        let query = doc! {
            "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() },
            "deleted": false
        };

        debug!(target: "request_repository", "Finding request with id: {:?}", id);

        let option = self
            .0
            .find_one(query, None)
            .await
            .map_err(Box::new)?;

        if option.is_none() {
            debug!(target: "request_repository", "[NOT FOUND] Request not found");
        }

        Ok(option.map(Into::into))
    }
}

#[async_trait]
impl Repository for RequestRepository {
    async fn find(&self, id: UUID) -> Result<Option<Request>, ApplicationError> {
        self.find(id)
            .await
            .map_err(ApplicationError::UnhandledError)
    }
}
