use crate::infrastructure::{mongodb::transformation_request::repository::RequestRepository, dependencies::mongodb::get_options};

pub type Repository = RequestRepository;

pub async fn create_repository() -> Repository {
    let options = get_options().await;

    RequestRepository::new(options.request_collection)
}
