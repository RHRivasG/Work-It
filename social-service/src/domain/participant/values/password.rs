use std::{convert::TryFrom, ops::Deref};

use crate::domain::{participant::errors::ParticipantError, shared::validation_helper::validate_password};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Password(String);

impl Password {
    pub fn new(value: String) -> Self {
        Password(value)
    }
}

impl<'a> TryFrom<&'a str> for Password {
    type Error = ParticipantError;

    fn try_from(value: &'a str) -> Result<Self, Self::Error> {
        if !validate_password(value) {
            return Err(ParticipantError::InvalidPasswordFormat);
        }

        Ok(Password(value.to_string()))
    }
}

#[allow(clippy::from_over_into)]
impl Into<String> for Password {
    fn into(self) -> String {
        self.0
    }
}

impl Deref for Password {
    type Target = str;

    fn deref(&self) -> &Self::Target {
        &*self.0
    }
}

#[cfg(test)]
mod tests {
    use std::{convert::TryInto, ops::Deref};

    use crate::domain::participant::errors::ParticipantError;

    use super::Password;

    #[test]
    fn fails_to_instantiate_invalid_password_special_chars() {
        let password: Result<Password, _> = "abcd1234A".try_into();

        assert!(password.is_err());
        assert_eq!(password.unwrap_err(), ParticipantError::InvalidPasswordFormat);
    }

    #[test]
    fn fails_to_instantiate_invalid_password_lowercase() {
        let password: Result<Password, _> = "ABCD1234/".try_into();

        assert!(password.is_err());
        assert_eq!(password.unwrap_err(), ParticipantError::InvalidPasswordFormat);
    }

    #[test]
    fn fails_to_instantiate_invalid_password_uppercase() {
        let password: Result<Password, _> = "abcd1234$".try_into();

        assert!(password.is_err());
        assert_eq!(password.unwrap_err(), ParticipantError::InvalidPasswordFormat);
    }

    #[test]
    fn fails_to_instantiate_invalid_password_digits() {
        let password: Result<Password, _> = "ABCDabcd$".try_into();

        assert!(password.is_err());
        assert_eq!(password.unwrap_err(), ParticipantError::InvalidPasswordFormat);
    }

    #[test]
    fn instantiates_for_valid_password() {
        let password: Result<Password, _> = "ABCDabcd$123".try_into();

        assert!(password.is_ok());
        assert_eq!(password.unwrap().deref(), "ABCDabcd$123");
    }
}
