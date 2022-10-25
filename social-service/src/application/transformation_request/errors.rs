use std::error::Error;

use crate::domain::transformation_request::errors::TransformationRequestError;

pub enum ApplicationError {
    DomainError(TransformationRequestError),
    AlreadyIssued,
    UnhandledError(Box<dyn Error + Send + Sync>)    
}
