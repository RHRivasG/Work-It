use serde::Deserialize;

#[derive(Deserialize)]
pub struct TrainerUpdateForm {
    pub name: String,
    pub preferences: Vec<String>
}
