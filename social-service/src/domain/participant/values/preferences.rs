use std::{convert::TryFrom, ops::Sub, collections::HashSet};

use crate::domain::{participant::errors::ParticipantError, shared::validation_helper::validate_preferences};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Preferences(HashSet<String>);

impl Preferences {
    pub fn new(value: Vec<String>) -> Self {
        Preferences(value.into_iter().collect())
    }

    pub fn to_vec(&self) -> Vec<String> {
        self.0.iter().cloned().collect() 
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

impl<'a> Sub for &'a Preferences {
    type Output = Preferences;

    fn sub(self, rhs: Self) -> Self::Output {
        let hashset = &self.0;
        let hashset_2 = &rhs.0;
        Preferences::new(hashset.difference(hashset_2).cloned().collect())
    }
}

#[cfg(test)]
mod tests {
    use std::convert::TryFrom;

    use super::Preferences;

    #[test]
    pub fn creates_preferences_with_at_least_3_tags() {
        let tags_slice: &[&str] = &["legs", "arms", "body"];
        let preferences: Result<Preferences, _> = Preferences::try_from(tags_slice);

        assert!(preferences.is_ok());
        assert_eq!(preferences.unwrap().to_vec(), ["legs", "arms", "body"]);
    }
    
    #[test]
    fn correctly_calculates_difference() {
        let pref_1 = Preferences::new(vec!["legs".to_string(), "arms".to_string()]);
        let pref_2 = Preferences::new(vec!["arms".to_string()]);

        assert_eq!(&pref_1 - &pref_2, Preferences::new(vec!["legs".to_string()]));
    }
}
