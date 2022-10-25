use std::{convert::TryFrom, collections::HashSet, ops::Sub};

use crate::domain::{
    shared::validation_helper::validate_preferences, trainer::errors::TrainerError,
};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Preferences(HashSet<String>);

impl Preferences {
    pub fn recreate(value: &[&str]) -> Self {
        Preferences(value.iter().map(ToString::to_string).collect())
    }

    pub fn to_vec(&self) -> Vec<String> {
        self.0.iter().cloned().collect() 
    }
}

impl<'a> Sub for &'a Preferences {
    type Output = Preferences;

    fn sub(self, rhs: Self) -> Self::Output {
        let hashset = &self.0;
        let hashset_2 = &rhs.0;
        Preferences(hashset.difference(hashset_2).cloned().collect())
    }
}

impl<'a> TryFrom<&'a [&'a str]> for Preferences {
    type Error = TrainerError;

    fn try_from(value: &'a [&'a str]) -> Result<Self, Self::Error> {
        if !validate_preferences(value) {
            return Err(TrainerError::InsufficientPreferences);
        }

        Ok(Preferences(value.iter().map(ToString::to_string).collect()))
    }
}
