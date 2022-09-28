use tide::Response;

use crate::domain::participant::errors::ParticipantError;

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
