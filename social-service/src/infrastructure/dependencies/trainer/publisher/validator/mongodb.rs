use crate::infrastructure::{mongodb::trainer::validator::MongodbValidator, dependencies::mongodb::get_options};

pub type ValidatorHandler = MongodbValidator;

pub async fn create_validator_handler() -> ValidatorHandler {
    let options = get_options().await;

    MongodbValidator(options.trainer_collection)
}
