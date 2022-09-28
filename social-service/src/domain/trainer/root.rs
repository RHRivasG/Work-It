use crate::domain::shared::uuid::UUID;

use super::{
    errors::TrainerError,
    values::{name::Name, password::Password, preferences::Preferences},
};

pub struct Trainer {
    id: UUID,
    name: Name,
    password: Password,
    preferences: Preferences,
}

impl Trainer {
    pub fn try_new<'a>(
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Self, TrainerError> {
        let name = name.try_into()?;
        let password = password.try_into()?;
        let preferences = preferences.try_into()?;

        Ok(Trainer {
            id: Default::default(),
            name,
            password,
            preferences,
        })
    }

    pub fn try_new_with_bytes_id<'a>(
        id: [u8; 16],
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Self, TrainerError> {
        let id: UUID = id.into();
        let name = name.try_into()?;
        let password = password.try_into()?;
        let preferences = preferences.try_into()?;

        Ok(Trainer {
            id,
            name,
            password,
            preferences,
        })
    }

    pub fn try_new_with_str_id<'a>(
        id: &'a str,
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Self, TrainerError> {
        let id = id.try_into().map_err(|_| TrainerError::InvalidUUID)?;
        let name = name.try_into()?;
        let password = password.try_into()?;
        let preferences = preferences.try_into()?;

        Ok(Trainer {
            id,
            name,
            password,
            preferences,
        })
    }

    pub fn set_name(&mut self, name: &str) -> Result<(), TrainerError> {
        let name = name.try_into()?;

        self.name = name;

        Ok(())
    }

    pub fn set_password(&mut self, password: &str) -> Result<(), TrainerError> {
        let password = password.try_into()?;

        self.password = password;

        Ok(())
    }

    pub fn set_preferences(&mut self, preferences: &[&str]) -> Result<(), TrainerError> {
        let preferences = preferences.try_into()?;

        self.preferences = preferences;

        Ok(())
    }
    
    pub fn id(&self) -> UUID {
        self.id
    }

    pub fn name(&self) -> &Name {
        &self.name
    }

    pub fn password(&self) -> &Password {
        &self.password
    }

    pub fn preferences(&self) -> &Preferences {
        &self.preferences
    }
}
