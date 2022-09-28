use async_trait::async_trait;
use base64::decode;
use tide::{Middleware, Next, Request, Response};

use crate::domain::shared::uuid::UUID;

pub struct DecodeIdMiddleware;

impl DecodeIdMiddleware {
    pub fn new() -> Self {
        DecodeIdMiddleware
    }

    fn try_decode_id<State>(&self, request: &mut Request<State>) -> Option<()> {
        let id = request.param("id").ok()?;
        let raw_uuid = decode(id).ok()?;
        let uuid: UUID = raw_uuid.as_slice().try_into().ok()?;

        request.set_ext(uuid);

        Some(())
    }
}

impl Default for DecodeIdMiddleware {
    fn default() -> Self {
        Self::new()
    }
}

#[async_trait]
impl<State> Middleware<State> for DecodeIdMiddleware 
    where State: Sync + Send + Clone + 'static
{
    async fn handle<'life0, 'life1>(
        &'life0 self,
        mut request: Request<State>,
        next: Next<'life1, State>,
    ) -> tide::Result
    where
        'life0: 'async_trait,
        'life1: 'async_trait,
        Self: 'async_trait,
    {
        match self.try_decode_id(&mut request) {
            Some(()) => Ok(next.run(request).await),
            None => Ok(Response::builder(400).into())
        }
    }
}
