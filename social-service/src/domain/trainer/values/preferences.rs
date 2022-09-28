use std::convert::TryFrom;

use crate::domain::{trainer::errors::TrainerError, shared::validation_helper::validate_preferences};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Preferences(Vec<String>);

impl<'a> TryFrom<&'a [&'a str]> for Preferences {
    type Error = TrainerError;

    fn try_from(value: &'a [&'a str]) -> Result<Self, Self::Error> {
        if !validate_preferences(value) {
            return Err(TrainerError::InsufficientPreferences);
        }

        Ok(Preferences(value.iter().map(ToString::to_string).collect()))
    }
}
