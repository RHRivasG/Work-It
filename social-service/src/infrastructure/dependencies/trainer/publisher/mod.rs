use crate::{
    application::trainer::errors::ApplicationError,
    domain::trainer::events::TrainerEvent,
    infrastructure::publisher::{
        builder::{generic::GenericPublisherBuilder, CanAdd, CanBuild},
        node::GenericPublisherNode,
    },
};

use self::{crud::create_crud_handler, validator::create_validator_handler};

pub mod crud;
pub mod validator;

pub type Publisher = GenericPublisherNode<TrainerEvent, ApplicationError>;

pub async fn create_publisher() -> Publisher {
    GenericPublisherBuilder::new(create_validator_handler().await)
        .add_handler(create_crud_handler().await)
        .build()
}
