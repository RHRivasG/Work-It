pub mod publisher;
pub mod repository;

use crate::application::participant::service::ApplicationService;
use self::{
    publisher::{create_publisher, Publisher},
    repository::{create_repository, Repository},
};

pub type UseCase = ApplicationService<Repository, Publisher>;

pub async fn create_service() -> UseCase {
    ApplicationService::new(create_repository().await, create_publisher().await)
}
