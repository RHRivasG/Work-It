use crate::domain::shared::uuid::UUID;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ParticipantEvent {
    ParticipantCreated {
        id: UUID,
        name: String,
        password: String,
        preferences: Vec<String>,
    },
    ParticipantNameUpdated {
        id: UUID,
        name: String,
        previous_name: String,
    },
    ParticipantPasswordUpdated {
        id: UUID,
        password: String,
        previous_password: String,
    },
    ParticipantPreferencesUpdated {
        id: UUID,
        added_preferences: Vec<String>,
        removed_preferences: Vec<String>,
    },
    ParticipantDeleted {
        id: UUID,
    },
    ParticipantTransformationRequestIssued {
        id: UUID,
    },
}
