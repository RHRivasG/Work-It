use async_trait::async_trait;

use crate::{domain::shared::uuid::UUID, application::transformation_request::errors::ApplicationError};

#[async_trait]
pub trait UseCase {
   async fn create_transformation_request(&self, id: UUID) -> Result<(), ApplicationError>;
   async fn accept_transformation_request(&self, id: UUID) -> Result<Option<()>, ApplicationError>;
   async fn reject_transformation_request(&self, id: UUID) -> Result<Option<()>, ApplicationError>;
   async fn delete_transformation_request(&self, id: UUID) -> Result<Option<()>, ApplicationError>;
}
