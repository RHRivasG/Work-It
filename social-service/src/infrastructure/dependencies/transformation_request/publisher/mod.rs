use crate::{
    application::transformation_request::errors::ApplicationError,
    domain::transformation_request::events::TransformationRequestEvent,
    infrastructure::publisher::{node::GenericPublisherNode, builder::{generic::GenericPublisherBuilder, CanAdd, CanBuild}},
};

use self::{validator::create_validator_handler, crud::create_crud_handler};

pub mod crud;
pub mod validator;
pub mod participant_handler;
pub mod trainer_handler;

pub type Publisher = GenericPublisherNode<TransformationRequestEvent, ApplicationError>;

pub async fn create_publisher() -> Publisher {
    GenericPublisherBuilder::new(create_validator_handler().await)
        .add_handler(create_crud_handler().await)
        .build()
}
