use chrono::{Utc, DateTime};

use crate::domain::shared::uuid::UUID;

#[derive(Debug, PartialEq, Eq, Clone)]
pub enum TransformationRequestEvent {
    TransformationRequestCreated { participant_id: UUID, issued_at: DateTime<Utc> },
    TransformationRequestApproved { participant_id: UUID, accepted_at: DateTime<Utc> },
    TransformationRequestRejected { participant_id: UUID, rejected_at: DateTime<Utc> },
    TransformationRequestDeleted { participant_id: UUID },
}
