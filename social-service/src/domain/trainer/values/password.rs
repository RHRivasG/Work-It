use std::{convert::TryFrom, ops::Deref};

use crate::domain::{trainer::errors::TrainerError, shared::validation_helper::validate_password};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Password(String);

impl<'a> TryFrom<&'a str> for Password {
    type Error = TrainerError;

    fn try_from(value: &'a str) -> Result<Self, Self::Error> {
        if !validate_password(value) {
            return Err(TrainerError::InvalidPassword);
        }

        Ok(Password(value.to_string()))
    }
}

impl Deref for Password {
    type Target = str;

    fn deref(&self) -> &Self::Target {
        &*self.0
    }
}
