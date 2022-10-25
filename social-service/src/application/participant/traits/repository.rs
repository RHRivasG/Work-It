use async_trait::async_trait;

use crate::domain::{participant::root::Participant, shared::uuid::UUID};

#[async_trait]
pub trait Repository {
    async fn find(&self, id: UUID) -> Option<Participant>;
    async fn get_all(&self) -> Vec<Participant>;
}
