use std::error::Error;
use crate::domain::participant::errors::ParticipantError;

#[derive(Debug)]
pub enum ApplicationError {
    DomainError(ParticipantError),
    IdTaken,
    RequestAlreadyIssued,
    NameTaken,
    UnhandledError(Box<dyn Error + Sync + Send>)
}
