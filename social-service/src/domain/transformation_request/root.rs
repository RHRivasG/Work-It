use std::borrow::Borrow;

use chrono::{DateTime, Utc};

use crate::domain::shared::uuid::UUID;

use super::events::TransformationRequestEvent;

#[derive(Debug, Default)]
pub struct Request {
    participant_id: UUID,
    issued_at: DateTime<Utc>,
    accepted_at: Option<DateTime<Utc>>,
    events: Vec<TransformationRequestEvent>,
}

impl Request {
    pub fn new(participant_id: UUID) -> Self {
        let issued_at = DateTime::default();
        Request {
            participant_id,
            issued_at,
            accepted_at: None,
            events: vec![TransformationRequestEvent::TransformationRequestCreated {
                participant_id: participant_id.to_vec().into_boxed_slice(),
                issued_at,
            }],
        }
    }

    pub fn accept(&mut self) {
        if None == self.accepted_at {
            let accepted_at = DateTime::default();
            self.accepted_at = accepted_at.into();
            self.events
                .push(TransformationRequestEvent::TransformationRequestApproved {
                    participant_id: self.participant_id().to_vec().into_boxed_slice(),
                    accepted_at,
                });
        }
    }

    pub fn issued_at(&self) -> DateTime<Utc> {
        self.issued_at
    }

    pub fn accepted_at(&self) -> Option<DateTime<Utc>> {
        self.accepted_at
    }

    pub fn participant_id(&self) -> UUID {
        self.participant_id
    }

    pub fn events(&self) -> &[TransformationRequestEvent] {
        self.events.borrow()
    }
}
