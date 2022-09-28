#[derive(PartialEq, Eq, Debug)]
pub enum ParticipantError {
    InvalidNameLength,
    InvalidPasswordLength,
    InvalidPasswordFormat,
    InsufficientPreferences,
    InvalidUUID
}
