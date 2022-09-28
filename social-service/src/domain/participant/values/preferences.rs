use std::{convert::TryFrom, ops::Deref};

use crate::domain::{participant::errors::ParticipantError, shared::validation_helper::validate_preferences};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Preferences(Vec<String>);

impl Preferences {
    pub fn new(value: Vec<String>) -> Self {
        Preferences(value)
    }
}

impl<'a> TryFrom<&'a [&'a str]> for Preferences {
    type Error = ParticipantError;

    fn try_from(value: &'a [&'a str]) -> Result<Self, Self::Error> {
        if !validate_preferences(value) {
            return Err(ParticipantError::InsufficientPreferences);
        }

        Ok(Preferences(value.iter().map(ToString::to_string).collect()))
    }
}

impl Deref for Preferences {
    type Target = [String];

    fn deref(&self) -> &Self::Target {
        &*self.0
    }
}

#[cfg(test)]
mod tests {
    use std::{convert::TryFrom, ops::Deref};

    use super::Preferences;

    #[test]
    pub fn creates_preferences_with_at_least_3_tags() {
        let tags_slice: &[&str] = &["legs", "arms", "body"];
        let preferences: Result<Preferences, _> = Preferences::try_from(tags_slice);

        assert!(preferences.is_ok());
        assert_eq!(preferences.unwrap().deref(), ["legs", "arms", "body"]);
    }
}
