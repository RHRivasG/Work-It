use crate::infrastructure::{mongodb::transformation_request::handler::RequestHandler, dependencies::mongodb::get_options};

pub type CrudHandler = RequestHandler;

pub async fn create_crud_handler() -> CrudHandler {
    let options = get_options().await;

    RequestHandler::new(options.request_collection)
}
