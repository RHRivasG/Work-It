use crate::application::transformation_request::service::ApplicationService;
use lazy_static::lazy_static;
use async_once::AsyncOnce;
use self::{repository::{Repository, create_repository}, publisher::{Publisher, create_publisher}};

pub mod publisher;
pub mod repository;

pub type UseCase = ApplicationService<Repository, Publisher>;

lazy_static! {
    static ref SERVICE: AsyncOnce<UseCase> = AsyncOnce::new(async {
        ApplicationService::new(create_repository().await, create_publisher().await)
    });
}

pub async fn create_service() -> UseCase {
    SERVICE.get().await.clone()
}
