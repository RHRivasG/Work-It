use crate::domain::shared::uuid::UUID;

use super::{
    errors::TrainerError,
    events::TrainerEvent,
    values::{name::Name, password::Password, preferences::Preferences},
};

pub struct Trainer {
    id: UUID,
    name: Name,
    password: Password,
    preferences: Preferences,
    events: Vec<TrainerEvent>,
}

impl Trainer {
    pub fn recreate<'a>(
        id: UUID,
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Self {
        let name = Name::recreate(name);
        let password = Password::recreate(password);
        let preferences = Preferences::recreate(preferences);

        Trainer {
            id,
            name,
            password,
            preferences,
            events: vec![],
        }
    }
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
            events: Vec::with_capacity(1),
        }
        .emit_creation_event())
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
            events: Vec::with_capacity(1),
        }
        .emit_creation_event())
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
            events: Vec::with_capacity(1),
        }
        .emit_creation_event())
    }

    pub fn try_new_with_id<'a>(
        id: UUID,
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Self, TrainerError> {
        let name = name.try_into()?;
        let password = password.try_into()?;
        let preferences = preferences.try_into()?;

        Ok(Trainer {
            id,
            name,
            password,
            preferences,
            events: Vec::with_capacity(1),
        }
        .emit_creation_event())
    }

    pub fn set_name(&mut self, name: &str) -> Result<(), TrainerError> {
        let previous_name = self.name().clone();
        let new_name = name.try_into()?;

        self.name = new_name;

        self.emit_name_updated_event(previous_name);

        Ok(())
    }

    pub fn set_password(&mut self, password: &str) -> Result<(), TrainerError> {
        let previous_password = self.password().clone();
        let password = password.try_into()?;

        self.password = password;

        self.emit_password_updated_event(previous_password);

        Ok(())
    }

    pub fn set_preferences(&mut self, preferences: &[&str]) -> Result<(), TrainerError> {
        let previous_preferences = self.preferences.clone();
        let new_preferences = preferences.try_into()?;

        self.preferences = new_preferences;

        self.emit_preferences_changed_event(&previous_preferences);

        Ok(())
    }

    pub fn delete(&mut self) {
        self.events
            .push(TrainerEvent::TrainerDeleted { id: self.id() })
    }

    fn emit_creation_event(mut self) -> Self {
        self.events.push(TrainerEvent::TrainerCreated {
            id: self.id(),
            name: self.name().to_string(),
            password: self.password().to_string(),
            preferences: self.preferences().to_vec(),
        });
        self
    }

    fn emit_name_updated_event(&mut self, previous_name: Name) {
        self.events.push(TrainerEvent::TrainerNameUpdated {
            id: self.id(),
            name: self.name().to_string(),
            previous_name: previous_name.to_string(),
        });
    }

    fn emit_password_updated_event(&mut self, previous_password: Password) {
        self.events.push(TrainerEvent::TrainerPasswordUpdated {
            id: self.id(),
            password: self.password().to_string(),
            previous_password: previous_password.to_string(),
        });
    }

    fn emit_preferences_changed_event(&mut self, previous_preferences: &Preferences) {
        self.events.push(TrainerEvent::TrainerPreferencesUpdated {
            id: self.id(),
            added_preferences: (previous_preferences - self.preferences()).to_vec(),
            removed_preferences: (self.preferences() - previous_preferences).to_vec(),
        });
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

    pub fn events(&self) -> Vec<TrainerEvent> {
        self.events.clone()
    }
}
