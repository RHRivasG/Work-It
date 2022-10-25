use async_trait::async_trait;
use crate::{domain::{shared::uuid::UUID, transformation_request::root::Request}, application::transformation_request::errors::ApplicationError};

#[async_trait]
pub trait Repository {
    async fn find(&self, id: UUID) -> Result<Option<Request>, ApplicationError>;
}
