use async_trait::async_trait;

use crate::{application::traits::publisher::Publisher, domain::participant::events::ParticipantEvent};

use super::mongodb::repository::MongodbRepository;

#[derive(Clone)]
pub struct ParticipantPublisher(MongodbRepository);

impl ParticipantPublisher {
    pub fn new(repo: MongodbRepository) -> Self {
        ParticipantPublisher(repo)
    }
}

#[async_trait]
impl Publisher<ParticipantEvent> for ParticipantPublisher {
   async fn publish(&self, evt: ParticipantEvent) {
       match evt {
           ParticipantEvent::ParticipantCreated { id, name, password, preferences } => self.0.create(id, name, password, preferences).await,
           ParticipantEvent::ParticipantUpdated { id, name, password, preferences } => self.0.update(id, name, password, preferences).await,
           ParticipantEvent::ParticipantDeleted { id } => self.0.delete(id).await,
           _ => ()
       }
   } 
}
