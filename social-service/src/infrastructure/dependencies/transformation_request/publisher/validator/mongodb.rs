use crate::infrastructure::{
    dependencies::mongodb::get_options,
    mongodb::transformation_request::validator::RequestValidator,
};

pub type ValidatorHandler = RequestValidator;

pub async fn create_validator_handler() -> ValidatorHandler {
    let options = get_options().await;

    RequestValidator::new(options.request_collection)
}
