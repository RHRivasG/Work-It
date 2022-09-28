use std::{convert::TryFrom, ops::Deref};

use crate::domain::{trainer::errors::TrainerError, shared::validation_helper::validate_name};

#[derive(Debug, PartialEq, Eq, Clone)]
pub struct Name(String);

impl<'a> TryFrom<&'a str> for Name {
    type Error = TrainerError;

    fn try_from(value: &'a str) -> Result<Self, Self::Error> {
        if !validate_name(value) {
            return Err(TrainerError::InvalidName);
        }
        
        Ok(Name(value.to_string()))
    }
}

impl<'a> Deref for Name {
    type Target = str;

    fn deref(&self) -> &Self::Target {
        &*self.0
    }
}
