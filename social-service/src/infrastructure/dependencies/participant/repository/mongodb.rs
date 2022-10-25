use crate::infrastructure::{
    dependencies::mongodb::get_options, mongodb::participant::repository::ParticipantRepository,
};

pub type Repository = ParticipantRepository;

pub async fn create_repository() -> Repository {
    let options = get_options().await;

    Repository::new(options.participant_collection).await
}
