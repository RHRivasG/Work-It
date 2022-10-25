use std::error::Error;

use log_err::LogErrResult;
use crate::{domain::trainer::errors::TrainerError, application::trainer::errors::ApplicationError};

impl From<TrainerError> for tide::Error {
    fn from(error: TrainerError) -> tide::Error {
        match error {
            TrainerError::InvalidUUID => tide::Error::from_str(400, "INVALID UUID FORMAT"),
            TrainerError::InvalidName => tide::Error::from_str(400, "INVALID NAME"),
            TrainerError::InvalidPassword => tide::Error::from_str(400, "INVALID PASSWORD"),
            TrainerError::InsufficientPreferences => tide::Error::from_str(400, "INSUFFICIENT PREFERENCES"),
        }
    }
}

impl From<ApplicationError> for tide::Error {
    fn from(error: ApplicationError) -> Self {
        match error {
            ApplicationError::DomainError(err) => err.into(),
            ApplicationError::IdTaken => tide::Error::from_str(409, "ID TAKEN"),
            ApplicationError::NameTaken => tide::Error::from_str(409, "NAME TAKEN"),
            ApplicationError::UnhandledError(err) => {
                let error_message = format!("{}", &err);
                Err::<(), Box<dyn Error>>(err).log_expect("[ERROR] Error occured on the domain layer");
                tide::Error::from_str(500, error_message)
            }
        }
    }
}
