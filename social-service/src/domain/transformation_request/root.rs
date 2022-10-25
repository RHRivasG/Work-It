use std::borrow::Borrow;

use chrono::{DateTime, Utc};

use crate::domain::shared::uuid::UUID;

use super::{errors::TransformationRequestError, events::TransformationRequestEvent};

#[derive(Debug, Default)]
pub struct Request {
    participant_id: UUID,
    issued_at: DateTime<Utc>,
    accepted_at: Option<DateTime<Utc>>,
    rejected_at: Option<DateTime<Utc>>,
    events: Vec<TransformationRequestEvent>,
}

impl Request {
    pub fn new(participant_id: UUID) -> Self {
        let issued_at = Utc::now();
        Request {
            participant_id,
            issued_at,
            accepted_at: None,
            rejected_at: None,
            events: vec![TransformationRequestEvent::TransformationRequestCreated {
                participant_id,
                issued_at,
            }],
        }
    }

    pub fn unsafe_new(
        participant_id: UUID,
        issued_at: DateTime<Utc>,
        accepted_at: Option<DateTime<Utc>>,
        rejected_at: Option<DateTime<Utc>>,
    ) -> Self {
        Request {
            participant_id,
            issued_at,
            accepted_at,
            rejected_at,
            events: vec![],
        }
    }

    pub fn accept(&mut self) -> Result<(), TransformationRequestError> {
        self.assert_untouched()?;

        let accepted_at = Utc::now();
        self.accepted_at = accepted_at.into();
        self.events
            .push(TransformationRequestEvent::TransformationRequestApproved {
                participant_id: self.participant_id(),
                accepted_at,
            });

        Ok(())
    }

    pub fn reject(&mut self) -> Result<(), TransformationRequestError> {
        self.assert_untouched()?;

        let rejected_at = Utc::now();
        self.rejected_at = rejected_at.into();
        self.events
            .push(TransformationRequestEvent::TransformationRequestRejected {
                participant_id: self.participant_id(),
                rejected_at,
            });

        Ok(())
    }

    pub fn delete(&mut self) {
        self.events
            .push(TransformationRequestEvent::TransformationRequestDeleted {
                participant_id: self.participant_id(),
            })
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

    fn assert_untouched(&self) -> Result<(), TransformationRequestError> {
        if self.accepted_at != None {
            return Err(TransformationRequestError::AlreadyAccepted);
        }

        if self.rejected_at != None {
            return Err(TransformationRequestError::AlreadyRejected);
        }

        Ok(())
    }
}
