use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize)]
pub struct ParticipantForm {
    pub name: String,
    pub password: String,
    pub preferences: Vec<String>
}
