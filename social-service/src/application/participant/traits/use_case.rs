use async_trait::async_trait;

use crate::{application::participant::{dto::ParticipantDto, errors::ApplicationError}, domain::shared::uuid::UUID};

type VoidResult = Result<(), ApplicationError>;

#[async_trait]
pub trait UseCase {
    async fn create<'a>(&self, name: &'a str, password: &'a str, preferences: &'a[&'a str]) -> VoidResult;
    async fn update<'a>(&self, id: UUID, name: &'a str, preferences: &'a[&'a str]) -> Result<Option<()>, ApplicationError>;
    async fn update_password<'a>(&self, id: UUID, password: &'a str) -> Result<Option<()>, ApplicationError>;
    async fn delete(&self, id: UUID) -> Result<Option<()>, ApplicationError>;
    async fn get(&self, id: UUID) -> Result<Option<ParticipantDto>, ApplicationError>;
    async fn get_all(&self) -> Result<Vec<ParticipantDto>, ApplicationError>;
    async fn request_transformation(&self, id: UUID) -> Result<Option<()>, ApplicationError>;
}
