use std::error::Error;

use log_err::LogErrResult;
use tide::Response;
use crate::{domain::participant::errors::ParticipantError, application::participant::errors::ApplicationError};

impl From<ParticipantError> for Response {
    fn from(error: ParticipantError) -> Response {
        match error {
            ParticipantError::InvalidUUID => Response::builder(400).body("FAILED TO PARSE ID").into(),
            ParticipantError::InvalidNameLength => Response::builder(400).body("INVALID NAME LENGTH").into(),
            ParticipantError::InvalidPasswordFormat => Response::builder(400).body("INVALID PASSWORD FORMAT").into(),
            ParticipantError::InvalidPasswordLength => Response::builder(400).body("INVALID PASSWORD LENGTH").into(),
            ParticipantError::InsufficientPreferences => Response::builder(400).body("INVALID PASSWORD LENGTH").into(),
        }
    }
}

impl From<ApplicationError> for Response {
    fn from(error: ApplicationError) -> Self {
        match error {
            ApplicationError::DomainError(err) => err.into(),
            ApplicationError::IdTaken => Response::builder(409).body("ID TAKEN").into(),
            ApplicationError::NameTaken => Response::builder(409).body("NAME TAKEN").into(),
            ApplicationError::RequestAlreadyIssued => Response::builder(409).into(),
            ApplicationError::UnhandledError(err) => {
                let error_message = format!("{}", &err);
                Err::<(), Box<dyn Error>>(err).log_expect("[ERROR] Error occured on the domain layer");
                Response::builder(500).body(error_message).into()
            }
        }
    }
}
