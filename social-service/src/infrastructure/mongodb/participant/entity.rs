use mongodb::bson::Binary;
use serde::{Deserialize, Serialize};
use std::convert::TryInto;

use crate::domain::participant::root::Participant;

#[derive(Debug, Serialize, Deserialize)]
pub(crate) struct ParticipantEntity {
    pub id: Binary,
    pub name: String,
    pub password: String,
    pub preferences: Vec<String>,
    pub deleted: bool,
}

impl From<ParticipantEntity> for Participant {
    fn from(entity: ParticipantEntity) -> Self {
        Participant::unsafe_new(
            entity.id.bytes.as_slice().try_into().unwrap(),
            entity.name.clone(),
            entity.password.clone(),
            entity.preferences.iter().map(ToString::to_string).collect(),
        )
    }
}
