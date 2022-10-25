use crate::infrastructure::{
    dependencies::mongodb::get_options, mongodb::participant::handler::ParticipantHandler,
};

pub type CrudHandler = ParticipantHandler;

pub async fn create_crud_handler() -> CrudHandler {
    let options = get_options().await;

    ParticipantHandler::new(options.participant_collection)
}
