use chrono::{Utc, DateTime};

type ImmutableUUID = Box<[u8]>;

#[derive(Debug, PartialEq, Eq)]
pub enum TransformationRequestEvent {
    TransformationRequestCreated { participant_id: ImmutableUUID, issued_at: DateTime<Utc> },
    TransformationRequestApproved { participant_id: ImmutableUUID, accepted_at: DateTime<Utc> },
}
