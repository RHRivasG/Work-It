use async_trait::async_trait;

use crate::{domain::{shared::uuid::UUID, trainer::root::Trainer}, application::trainer::errors::ApplicationError};

#[async_trait]
pub trait Repository {
   async fn get(&self, id: UUID) -> Result<Option<Trainer>, ApplicationError>;
   async fn get_all(&self) -> Result<Vec<Trainer>, ApplicationError>;
}
