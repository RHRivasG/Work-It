use std::error::Error;

use crate::domain::trainer::errors::TrainerError;

#[derive(Debug)]
pub enum ApplicationError {
    DomainError(TrainerError),
    IdTaken,
    NameTaken,
    UnhandledError(Box<dyn Error + Sync + Send>)
}
