use base64::encode;
use serde::Serialize;

use crate::application::trainer::dto::TrainerDto;

#[derive(Serialize)]
pub struct TrainerViewModel {
    id: String,
    name: String,
    preferences: Vec<String>,
}

impl From<TrainerDto> for TrainerViewModel {
    fn from(dto: TrainerDto) -> Self {
        TrainerViewModel {
            id: encode(dto.id),
            name: dto.name,
            preferences: dto.preferences,
        }
    }
}
