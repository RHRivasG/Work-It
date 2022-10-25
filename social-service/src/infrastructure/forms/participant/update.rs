use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ParticipantUpdateForm {
    pub name: String,
    pub preferences: Vec<String>
}
