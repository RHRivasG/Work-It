use async_trait::async_trait;
use futures::StreamExt;
use mongodb::{
    bson::{doc, Binary, spec::BinarySubtype},
    options::{ClientOptions, DeleteOptions, FindOneOptions, InsertOneOptions, UpdateOptions},
    Client, Collection,
};
use std::{sync::Arc, convert::TryInto};

use crate::{
    application::participant::traits::repository::Repository,
    domain::{participant::root::Participant, shared::uuid::UUID},
};

use super::model::ParticipantEntity;

#[derive(Clone)]
pub struct MongodbRepository(Collection<ParticipantEntity>);

impl MongodbRepository {
    pub async fn new(host: &str, db: &str, collection: &str) -> Self {
        let options = ClientOptions::parse(format!("mongodb://{}", host))
            .await
            .unwrap();
        let client = Client::with_options(options).unwrap();
        let db = client.database(db);

        MongodbRepository(db.collection(collection))
    }

    pub async fn create(
        &self,
        id: Box<[u8]>,
        name: Box<str>,
        password: Box<str>,
        preferences: Box<[Box<str>]>,
    ) {
        let entity = ParticipantEntity {
            id: Binary{ subtype: BinarySubtype::Uuid, bytes: id.into() },
            name,
            password,
            preferences,
        };
        self.0
            .insert_one(entity, InsertOneOptions::default())
            .await
            .unwrap();
    }

    pub async fn update(
        &self,
        id: Box<[u8]>,
        name: Box<str>,
        password: Box<str>,
        preferences: Box<[Box<str>]>,
    ) {
        let update = doc! {
            "name": name.to_string(),
            "password": password.to_string(),
            "preferences": preferences.iter().map(ToString::to_string).collect::<Vec<String>>()
        };
        let uuid: UUID = (&*id).try_into().unwrap();

        let query = doc! { "id": uuid.to_string() };

        self.0
            .update_one(query, update, UpdateOptions::default())
            .await
            .unwrap();
    }

    pub async fn delete(&self, id: Box<[u8]>) {
        let uuid: UUID = (&*id).try_into().unwrap();

        let query = doc! {
            "id": uuid.to_string()
        };

        self.0
            .delete_one(query, DeleteOptions::default())
            .await
            .unwrap();
    }
}

#[async_trait]
impl Repository for MongodbRepository {
    async fn find(&self, id: &str) -> Option<Participant> {
        let uuid: UUID = id.try_into().unwrap();
        let filter = doc! { "id": Binary { subtype: BinarySubtype::Uuid, bytes: uuid.to_vec() } };
        let result = self.0.find_one(filter, FindOneOptions::default()).await;
        let option = result.unwrap();

        option.map(Into::into)
    }

    async fn get_all(&self) -> Vec<Participant> {
        let cursor = self.0.find(None, None).await.unwrap();
        cursor.map(|el| el.unwrap().into()).collect().await
    }
}

#[async_trait]
impl Repository for Arc<MongodbRepository> {
    async fn find(&self, id: &str) -> Option<Participant> {
        (&*self).find(id).await
    }

    async fn get_all(&self) -> Vec<Participant> {
        (&*self).get_all().await
    }
}
