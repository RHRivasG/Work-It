use std::error::Error;

use log_err::LogErrResult;
use tide::Response;
use crate::{application::transformation_request::errors::ApplicationError, domain::transformation_request::errors::TransformationRequestError, };

impl From<TransformationRequestError> for Response {
    fn from(err: TransformationRequestError) -> Self {
        match err {
            TransformationRequestError::AlreadyAccepted => Response::builder(409).into(),
            TransformationRequestError::AlreadyRejected => Response::builder(409).into(),
        }
    }
}

impl From<ApplicationError> for Response {
    fn from(err: ApplicationError) -> Self {
        match err {
            ApplicationError::DomainError(err) => err.into(),
            ApplicationError::AlreadyIssued => Response::builder(409).into(),
            ApplicationError::UnhandledError(err) => {
                let error_message = format!("{}", &err);
                Err::<(), Box<dyn Error>>(err).log_expect("[ERROR] Error occured on the domain layer");
                Response::builder(500).body(error_message).into()
            }
        }
    }
}
