use std::borrow::Borrow;

use crate::domain::shared::uuid::UUID;

use super::{
    errors::ParticipantError,
    events::ParticipantEvent,
    values::{name::Name, password::Password, preferences::Preferences},
};

#[derive(Debug, Clone)]
pub struct Participant {
    name: Name,
    password: Password,
    preferences: Preferences,
    id: UUID,
    events: Vec<ParticipantEvent>,
}

impl Participant {
    pub fn unsafe_new(id: UUID, name: String, password: String, preferences: Vec<String>) -> Self {
        let name = Name::new(name);
        let password = Password::new(password);
        let preferences = Preferences::new(preferences);

        Participant {
            id,
            name,
            password,
            preferences,
            events: vec![],
        }
    }

    pub fn try_new_with_str_id<'a>(
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
        id: &'a str,
    ) -> Result<Self, ParticipantError> {
        let name: Name = name.try_into()?;
        let password: Password = password.try_into()?;
        let preferences: Preferences = preferences.try_into()?;
        let id: UUID = id.try_into().map_err(|_| ParticipantError::InvalidUUID)?;
        let event = Participant::new_creation_event(
            name.clone(),
            password.clone(),
            preferences.clone(),
            id,
        );

        Ok(Participant {
            name,
            password,
            preferences,
            id,
            events: vec![event],
        })
    }

    pub fn try_new_with_id<'a>(
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
        id: [u8; 16],
    ) -> Result<Self, ParticipantError> {
        let name: Name = name.try_into()?;
        let password: Password = password.try_into()?;
        let preferences: Preferences = preferences.try_into()?;
        let id: UUID = id.into();
        let event = Participant::new_creation_event(
            name.clone(),
            password.clone(),
            preferences.clone(),
            id,
        );

        Ok(Participant {
            id,
            name,
            password,
            preferences,
            events: vec![event],
        })
    }

    pub fn try_new<'a>(
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Self, ParticipantError> {
        let name: Name = name.try_into()?;
        let password: Password = password.try_into()?;
        let preferences: Preferences = preferences.try_into()?;
        let id: UUID = Default::default();
        let event = Participant::new_creation_event(
            name.clone(),
            password.clone(),
            preferences.clone(),
            id,
        );

        Ok(Participant {
            name,
            password,
            preferences,
            id,
            events: vec![event],
        })
    }

    pub fn set_name(&mut self, new_name: &str) -> Result<(), ParticipantError> {
        let previous_name = self.name().clone();
        let new_name = new_name.try_into()?;
        self.name = new_name;
        self.events.push(ParticipantEvent::ParticipantNameUpdated {
            id: self.id(),
            name: self.name().to_string(),
            previous_name: previous_name.into()
        });

        Ok(())
    }

    pub fn set_password(&mut self, new_password: &str) -> Result<(), ParticipantError> {
        let previous_password = self.password().clone();
        let new_password = new_password.try_into()?;
        self.password = new_password;
        self.events
            .push(ParticipantEvent::ParticipantPasswordUpdated {
                id: self.id(),
                password: self.password().to_string(),
                previous_password: previous_password.into()
            });

        Ok(())
    }

    pub fn set_preferences(&mut self, new_preferences: &[&str]) -> Result<(), ParticipantError> {
        let new_preferences: Preferences = new_preferences.try_into()?;
        let old_preferences = &self.preferences;

        self.events
            .push(ParticipantEvent::ParticipantPreferencesUpdated {
                id: self.id(),
                added_preferences: (&new_preferences - old_preferences).to_vec(),
                removed_preferences: (old_preferences - &new_preferences).to_vec(),
            });

        Ok(())
    }

    pub fn issue_transformation_request(&mut self) {
        self.events
            .push(ParticipantEvent::ParticipantTransformationRequestIssued { id: self.id() });
    }

    pub fn delete(&mut self) {
        self.events.push(ParticipantEvent::ParticipantDeleted {
            id: self.id(),
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

    pub fn events(&self) -> &[ParticipantEvent] {
        self.events.borrow()
    }

    fn new_creation_event(
        name: Name,
        password: Password,
        preferences: Preferences,
        id: UUID,
    ) -> ParticipantEvent {
        ParticipantEvent::ParticipantCreated {
            id,
            name: name.to_string(),
            password: password.to_string(),
            preferences: preferences.to_vec(),
        }
    }
}

#[cfg(test)]
mod tests {
    use std::{convert::TryFrom, ops::Deref};

    use crate::domain::shared::uuid::UUID;

    use super::Participant;

    #[test]
    pub fn creates_with_default_id() {
        let participant_result =
            Participant::try_new("Michael Nelo", "KHearts358/2", &["legs", "arms", "body"]);

        assert!(participant_result.is_ok());

        let participant = participant_result.unwrap();

        assert_eq!(participant.name().deref(), "Michael Nelo");
        assert_eq!(participant.password().deref(), "KHearts358/2");
        assert_eq!(
            participant.preferences().to_vec(),
            vec!["legs", "arms", "body"]
        );

        let raw_uuid = participant.id().to_string();

        assert_eq!(participant.id(), UUID::try_from(&*raw_uuid).unwrap());
    }

    #[test]
    pub fn creates_with_byte_id() {
        let participant_result = Participant::try_new_with_id(
            "Michael Nelo",
            "KHearts358/2",
            &["legs", "arms", "body"],
            [
                0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
                0xaf, 0xf6,
            ],
        );

        assert!(participant_result.is_ok());

        let participant = participant_result.unwrap();

        assert_eq!(participant.name().deref(), "Michael Nelo");
        assert_eq!(participant.password().deref(), "KHearts358/2");
        assert_eq!(
            participant.preferences().to_vec(),
            vec!["legs", "arms", "body"]
        );
        assert_eq!(
            participant.id(),
            UUID::from([
                0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
                0xaf, 0xf6,
            ])
        );
    }

    #[test]
    pub fn creates_with_str_id() {
        let participant_result = Participant::try_new_with_str_id(
            "Michael Nelo",
            "KHearts358/2",
            &["legs", "arms", "body"],
            "971f2a79a086-46b0-9c9c-dbc2fc74aff6",
        );

        assert!(participant_result.is_ok());

        let participant = participant_result.unwrap();

        assert_eq!(participant.name().deref(), "Michael Nelo");
        assert_eq!(participant.password().deref(), "KHearts358/2");
        assert_eq!(
            participant.preferences().to_vec(),
            vec!["legs", "arms", "body"]
        );
        assert_eq!(
            participant.id(),
            UUID::try_from("971f2a79a086-46b0-9c9c-dbc2fc74aff6").unwrap()
        );
    }

    #[test]
    fn sets_name_correctly() {
        let mut participant = Participant::try_new_with_str_id(
            "Michael Nelo",
            "KHearts358/2",
            &["legs", "arms", "body"],
            "971f2a79a086-46b0-9c9c-dbc2fc74aff6",
        )
        .unwrap();

        participant.set_name("Ruth Rivas").unwrap();

        assert_eq!(participant.name().deref(), "Ruth Rivas");
    }

    #[test]
    fn sets_password_correctly() {
        let mut participant = Participant::try_new_with_str_id(
            "Michael Nelo",
            "KHearts358/2",
            &["legs", "arms", "body"],
            "971f2a79a086-46b0-9c9c-dbc2fc74aff6",
        )
        .unwrap();

        participant.set_password("abc1234$A").unwrap();

        assert_eq!(participant.password().deref(), "abc1234$A");
    }

    #[test]
    fn sets_preferences_correctly() {
        let mut participant = Participant::try_new_with_str_id(
            "Michael Nelo",
            "KHearts358/2",
            &["legs", "arms", "body"],
            "971f2a79a086-46b0-9c9c-dbc2fc74aff6",
        )
        .unwrap();

        participant
            .set_preferences(&["legs", "arms", "quads"])
            .unwrap();

        assert_eq!(
            participant.preferences().to_vec(),
            vec!["legs", "arms", "quads"]
        );
    }
}
