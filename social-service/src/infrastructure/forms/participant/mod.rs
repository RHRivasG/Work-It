pub mod password_update;
pub mod update;

use serde::{Serialize, Deserialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ParticipantForm {
    pub name: String,
    pub password: String,
    pub preferences: Vec<String>
}
