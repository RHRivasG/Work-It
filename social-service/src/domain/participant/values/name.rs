use std::{convert::TryFrom, ops::Deref};

use crate::domain::{participant::errors::ParticipantError, shared::validation_helper::validate_name};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Name(String);

impl Name { 
    pub fn new(value: String) -> Self {
        Name(value)
    }
}

impl<'a> TryFrom<&'a str> for Name {
    type Error = ParticipantError;

    fn try_from(value: &'a str) -> Result<Self, Self::Error> {
        if !validate_name(value) {
            return Err(ParticipantError::InvalidNameLength);
        }

        Ok(Name(value.to_string()))
    }
}

#[allow(clippy::from_over_into)]
impl Into<String> for Name {
    fn into(self) -> String {
        self.0
    }
}

impl Deref for Name {
    type Target = str;

    fn deref(&self) -> &Self::Target {
        &*self.0
    }
}

#[cfg(test)]
mod tests {
    use std::{ops::Deref, convert::TryInto};

    use crate::domain::participant::values::name::Name;

    #[test]
    pub fn creates_name_with_length_under_50_chars() {
        let name: Result<Name, _> = "A".try_into();

        assert!(name.is_ok());
        assert_eq!(name.unwrap().deref(), "A");
    }

    #[test]
    pub fn copy_modification_does_not_affect_original() {
        let name: Name = "A".try_into().unwrap();
        let mut owned_name = name.to_string();

        owned_name.push('B');


        assert_eq!(&*name, "A");
        assert_eq!(owned_name, "AB");
    }
}
