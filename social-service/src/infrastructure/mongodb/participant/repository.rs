use async_trait::async_trait;
use futures::StreamExt;
use log_err::LogErrResult;
use mongodb::bson::{doc, spec::BinarySubtype, Binary};

use mongodb::{Collection, IndexModel, options::IndexOptions};
use tide::log::debug;
use crate::domain::shared::uuid::UUID;

use crate::{
    application::participant::traits::repository::Repository,
    domain::participant::root::Participant,
};

use super::entity::ParticipantEntity;

#[derive(Clone)]
pub struct ParticipantRepository(Collection<ParticipantEntity>);

impl ParticipantRepository {
    pub(crate) async fn new(collection: Collection<ParticipantEntity>) -> Self 
    {
        let index = IndexModel::builder()
            .keys(doc! {
                "id": 1,
                "name": 1
            })
            .options(Some(IndexOptions::builder().unique(true).build()))
            .build();

        collection.create_index(index, None).await.log_expect("Could not create index for participant id and name");

        ParticipantRepository(collection)
    }

    async fn get_by_id(&self, id: UUID) -> Option<Participant> {
        let filter = doc! { "id": Binary { subtype: BinarySubtype::Uuid, bytes: id.to_vec() }, "deleted": false };

        debug!(target: "participant_repository", "Finding participant with ID {}", id.to_string());

        let result = self.0.find_one(filter, None).await;
        let option = result.unwrap();

        if option.is_none() {
            debug!(target: "participant_repository", "[NOT FOUND] Participant not found!")
        }

        option.map(Into::into)
    }

    async fn all(&self) -> Vec<Participant> {
        let cursor = self.0.find(doc! { "deleted": false }, None).await.unwrap();

        debug!(target: "participant_repository", "Retrieving all participants");

        cursor.map(|el| el.unwrap().into()).collect().await
    }
}

#[async_trait]
impl Repository for ParticipantRepository {
    async fn find(&self, id: UUID) -> Option<Participant> {
        self.get_by_id(id).await
    }

    async fn get_all(&self) -> Vec<Participant> {
        self.all().await
    }
}
