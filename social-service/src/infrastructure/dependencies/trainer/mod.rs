use crate::application::trainer::service::ApplicationService;
use async_once::AsyncOnce;
use lazy_static::lazy_static;
use self::{repository::{Repository, create_repository}, publisher::{Publisher, create_publisher}};

pub mod repository;
pub mod publisher;

pub type UseCase = ApplicationService<Repository, Publisher>;

lazy_static! {
    static ref USE_CASE: AsyncOnce<UseCase> = AsyncOnce::new(async {
        ApplicationService::new(create_repository().await, create_publisher().await)
    });
}

pub async fn create_service() -> UseCase {
    USE_CASE.get().await.clone()
}
