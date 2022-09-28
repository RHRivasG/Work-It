use mongodb::bson::Binary;
use serde::{Deserialize, Serialize};
use std::convert::TryInto;

use crate::domain::participant::root::Participant;

#[derive(Serialize, Deserialize)]
pub(crate) struct ParticipantEntity {
    pub id: Binary,
    pub name: Box<str>,
    pub password: Box<str>,
    pub preferences: Box<[Box<str>]>,
}

impl From<ParticipantEntity> for Participant {
    fn from(entity: ParticipantEntity) -> Self {
        Participant::unsafe_new(
            entity.id.bytes.as_slice().try_into().unwrap(),
            entity.name.into(),
            entity.password.into(),
            entity.preferences.iter().map(ToString::to_string).collect(),
        )
    }
}
