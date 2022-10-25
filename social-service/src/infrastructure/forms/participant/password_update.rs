use serde::{Serialize, Deserialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ParticipantPasswordUpdateForm {
    pub password: String,
}
