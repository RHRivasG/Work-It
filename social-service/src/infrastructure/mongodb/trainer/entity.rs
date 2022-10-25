use mongodb::bson::{Binary, spec::BinarySubtype};
use serde::{Serialize, Deserialize};

use crate::domain::{shared::uuid::UUID, trainer::root::Trainer};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct TrainerEntity {
    pub id: Binary,
    pub name: String,
    pub password: String,
    pub preferences: Vec<String>,
    pub deleted: bool,
}

impl TrainerEntity {
    pub fn new(id: UUID, name: String, password: String, preferences: Vec<String>) -> Self {
        TrainerEntity {
            id: Binary {
                subtype: BinarySubtype::Uuid,
                bytes: id.to_vec(),
            },
            name,
            password,
            preferences,
            deleted: false,
        }
    }
}

impl From<TrainerEntity> for Trainer {
    fn from(entity: TrainerEntity) -> Self {
        let id = entity.id.bytes.as_slice().try_into().unwrap();

        Trainer::recreate(
            id,
            entity.name.as_str(),
            entity.password.as_str(),
            entity
                .preferences
                .iter()
                .map(AsRef::as_ref)
                .collect::<Vec<&str>>()
                .as_slice(),
        )
    }
}
