pub mod tide;

use social_service::{
    application::participant::service::ApplicationService,
    infrastructure::{mongodb::repository::MongodbRepository, publisher::ParticipantPublisher},
};

#[cfg(feature = "tide")]
use crate::tide::server;

#[async_std::main]
pub async fn main() -> std::io::Result<()> {
    let repository = if cfg!(feature = "mongodb") {
        MongodbRepository::new("localhost:27017", "work-it", "participants").await
    } else {
        panic!("Only the mongodb repository is implemented")
    };

    let publisher = if cfg!(feature = "mongodb") {
        ParticipantPublisher::new(repository.clone())
    } else {
        panic!("Only the mongodb publisher is implemented")
    };

    let state = ApplicationService::new(repository, publisher);

    server(state).await?;
    Ok(())
}
