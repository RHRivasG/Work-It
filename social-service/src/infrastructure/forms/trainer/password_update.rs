use serde::Deserialize;

#[derive(Deserialize)]
pub struct TrainerPasswordUpdateForm {
    pub password: String
}
