use crate::infrastructure::{mongodb::trainer::handler::MongodbHandler, dependencies::mongodb::get_options};

pub type CrudHandler = MongodbHandler;

pub async fn create_crud_handler() -> CrudHandler {
    let options = get_options().await;
    
    MongodbHandler(options.trainer_collection)
}
