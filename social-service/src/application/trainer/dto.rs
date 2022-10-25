use serde::Serialize;

use crate::domain::trainer::root::Trainer;

#[derive(Serialize)]
pub struct TrainerDto {
    pub id: String,
    pub name: String,
    pub preferences: Vec<String>
}

impl From<Trainer> for TrainerDto {
    fn from(trainer: Trainer) -> Self {
        TrainerDto {
            id: trainer.id().to_string(),
            name: trainer.name().to_string(),
            preferences: trainer.preferences().to_vec()
        }
    }
}
