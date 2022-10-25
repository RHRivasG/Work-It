pub mod crud;
pub mod validator;
pub mod transformation_requests;

use crate::{
    application::participant::errors::ApplicationError,
    domain::participant::events::ParticipantEvent,
    infrastructure::publisher::{node::GenericPublisherNode, builder::{CanBuild, CanAdd, generic::GenericPublisherBuilder}},
};

use self::{crud::create_crud_handler, validator::create_validator_handler, transformation_requests::create_requests_handler};

pub type Publisher = GenericPublisherNode<ParticipantEvent, ApplicationError>;

pub async fn create_publisher() -> Publisher {
    let mut builder = GenericPublisherBuilder::new(create_validator_handler().await);

    builder
        .add_handler(create_crud_handler().await)
        .add_handler(create_requests_handler().await)
        .build()
}
