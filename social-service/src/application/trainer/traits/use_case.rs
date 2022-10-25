use async_trait::async_trait;

use crate::{application::trainer::{errors::ApplicationError, dto::TrainerDto}, domain::shared::uuid::UUID};

#[async_trait]
pub trait UseCase {
    async fn create<'a>(&self, id: UUID, name: &'a str, password: &'a str, preferences: &'a [&'a str]) -> Result<(), ApplicationError>;
    async fn update<'a>(&self, id: UUID, name: &'a str, preferences: &'a [&'a str]) -> Result<Option<()>, ApplicationError>;
    async fn update_password<'a>(&self, id: UUID, password: &'a str) -> Result<Option<()>, ApplicationError>;
    async fn delete(&self, id: UUID) -> Result<Option<()>, ApplicationError>;
    async fn get(&self, id: UUID) -> Result<Option<TrainerDto>, ApplicationError>;
    async fn get_all(&self) -> Result<Vec<TrainerDto>, ApplicationError>;
}
