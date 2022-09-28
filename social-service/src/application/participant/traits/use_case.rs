use async_trait::async_trait;

use crate::{application::participant::dto::ParticipantDto, domain::participant::errors::ParticipantError};

type VoidResult = Result<(), ParticipantError>;

#[async_trait]
pub trait UseCase {
    async fn create<'a>(&self, name: &'a str, password: &'a str, preferences: &'a[&'a str]) -> VoidResult;
    async fn update<'a>(&self, id: &'a str, name: &'a str, password: &'a str, preferences: &'a[&'a str]) -> Result<Option<()>, ParticipantError>;
    async fn delete(&self, id: &str) -> Result<Option<()>, ParticipantError>;
    async fn get(&self, id: &str) -> Option<ParticipantDto>;
    async fn get_all(&self) -> Vec<ParticipantDto>;
    async fn request_transformation(&self, id: &str) -> Result<Option<()>, ParticipantError>;
}
