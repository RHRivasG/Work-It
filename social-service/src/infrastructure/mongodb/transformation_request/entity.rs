use mongodb::bson::{Binary, DateTime};
use serde::{Deserialize, Serialize};

use crate::domain::transformation_request::root::Request;

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct RequestEntity {
    pub id: Binary,
    pub issued_at: DateTime,
    pub accepted_at: Option<DateTime>,
    pub rejected_at: Option<DateTime>,
    pub deleted: bool
}

impl From<RequestEntity> for Request {
    fn from(entity: RequestEntity) -> Self {
        Request::unsafe_new(
            entity.id.bytes.as_slice().try_into().unwrap(),
            entity.issued_at.into(),
            entity.accepted_at.map(Into::into),
            entity.rejected_at.map(Into::into),
        )
    }
}
