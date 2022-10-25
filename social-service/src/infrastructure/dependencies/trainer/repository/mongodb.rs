use crate::infrastructure::{mongodb::trainer::repository::MongodbRepository, dependencies::mongodb::get_options};

pub type Repository = MongodbRepository;

pub async fn create_repository() -> Repository {
    let options = get_options().await;

    MongodbRepository(options.trainer_collection)
}
