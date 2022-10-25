use crate::infrastructure::{mongodb::participant::validator::ParticipantValidator, dependencies::mongodb::get_options};

pub type ValidatorHandler = ParticipantValidator;

pub async fn create_validator_handler() -> ValidatorHandler {
    let options = get_options().await;

    ValidatorHandler::new(options.participant_collection)
}
