use async_trait::async_trait;
use futures::Stream;

use crate::domain::participant::root::Participant;

#[async_trait]
pub trait Repository {
    async fn find(&self, id: &str) -> Option<Participant>;
    async fn get_all(&self) -> Vec<Participant>;
}
