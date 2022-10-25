use serde::{Serialize, Deserialize};
use base64::encode;

use crate::application::participant::dto::ParticipantDto;

#[derive(Serialize, Deserialize)]
pub struct ParticipantViewModel {
    pub id: String,
    pub name: String,
    pub preferences: Vec<String>
}

impl From<ParticipantDto> for ParticipantViewModel {
    fn from(dto: ParticipantDto) -> Self {
        ParticipantViewModel {
            id: encode(dto.id),
            name: dto.name.into(),
            preferences: dto.preferences.iter().map(ToString::to_string).collect()
        }
    }
}

pub mod tide {
    use tide::{Response, Body};

    use super::ParticipantViewModel;

    impl TryFrom<ParticipantViewModel> for Response {
        type Error = tide::Error;

        fn try_from(vm: ParticipantViewModel) -> Result<Self, Self::Error> {
            let body = Body::from_json(&vm)?;

            Ok(body.into())
        }
    }
}
