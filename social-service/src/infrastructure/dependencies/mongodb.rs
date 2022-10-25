use std::time::Duration;

use async_once::AsyncOnce;
use async_std::future::timeout;
use config::{Config, File};
use lazy_static::lazy_static;
use log_err::LogErrResult;
use mongodb::{options::ClientOptions, Client};
use serde::Deserialize;

use crate::infrastructure::mongodb::options::MongodbOptions;

#[derive(Deserialize, Clone)]
struct Mongodb {
    url: String,
    db: String,
    participant_collection: String,
    request_collection: String,
    trainer_collection: String
}

#[derive(Deserialize, Clone)]
struct Settings {
    database: Mongodb,
}

impl Settings {
    pub fn from_config() -> Self {
        let mongodb: Settings = Config::builder()
            .add_source(File::with_name("config/default.toml"))
            .build()
            .unwrap()
            .try_deserialize()
            .unwrap();

        mongodb
    }
}

lazy_static! {
    static ref OPTIONS: AsyncOnce<MongodbOptions> = AsyncOnce::new(async {
        let Settings { database: mongodb } = Settings::from_config();
        let options = timeout(Duration::from_secs(5), ClientOptions::parse(mongodb.url))
            .await
            .log_expect("Timeout connecting to MongoDB")
            .log_expect("Cannot connect to MongoDB server");
        let client = Client::with_options(options).log_expect("Could not create mongodb client");
        let database = client.database(&*mongodb.db);
        let participant_collection = database.collection(&*mongodb.participant_collection);
        let request_collection = database.collection(&*mongodb.request_collection);
        let trainer_collection = database.collection(&*mongodb.trainer_collection);

        MongodbOptions::new(participant_collection, request_collection, trainer_collection)
    });
}

pub(crate) async fn get_options() -> MongodbOptions {
    OPTIONS.get().await.clone()
}
