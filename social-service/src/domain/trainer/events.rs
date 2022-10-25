use crate::domain::shared::uuid::UUID;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TrainerEvent {
    TrainerCreated {
        id: UUID,
        name: String,
        password: String,
        preferences: Vec<String>,
    },
    TrainerNameUpdated {
        id: UUID,
        name: String,
        previous_name: String,
    },
    TrainerPasswordUpdated {
        id: UUID,
        password: String,
        previous_password: String,
    },
    TrainerPreferencesUpdated {
        id: UUID,
        added_preferences: Vec<String>,
        removed_preferences: Vec<String>,
    },
    TrainerDeleted {
        id: UUID,
    }
}
