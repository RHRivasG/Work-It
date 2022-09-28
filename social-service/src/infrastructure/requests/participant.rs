use std::convert::TryInto;

use base64::decode;
use serde::{Serialize, Deserialize};

use crate::domain::shared::uuid::UUID;

#[derive(Serialize, Deserialize)]
pub struct FindParticipantRequest {
    id: String
}

impl FindParticipantRequest {
    pub fn new(id: &str) -> Self {
        FindParticipantRequest { 
            id: id.to_owned()
        }
    }

    pub fn id(&self) -> Option<String> {
        let raw_uuid = decode(&self.id).ok()?;
        let uuid: UUID = raw_uuid.as_slice().try_into().ok()?;

        uuid.to_string().into()
    }
}
