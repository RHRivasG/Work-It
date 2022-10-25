use std::{collections::LinkedList, sync::Arc};

use async_trait::async_trait;
use tide::log::debug;

use crate::application::traits::publisher::Publisher;

use super::handler::Handler;

pub struct GenericPublisherNode<E, Err>(Arc<LinkedList<Box<dyn Handler<E, Err> + Sync + Send>>>);

impl<E, Err> Clone for GenericPublisherNode<E, Err> {
    fn clone(&self) -> Self {
        GenericPublisherNode(Arc::clone(&self.0))
    }
}

impl<E, Err> GenericPublisherNode<E, Err> {
    pub fn new<H>(handler: H) -> Self
    where
        H: 'static + Handler<E, Err> + Send + Sync,
    {
        let handler: Box<dyn Handler<E, Err> + Sync + Send> = Box::new(handler);
        GenericPublisherNode(Arc::new(LinkedList::from([handler])))
    }

    pub fn add<H>(&mut self, next: H)
    where
        H: 'static + Handler<E, Err> + Send + Sync,
    {
        let handler: Box<dyn Handler<E, Err> + Sync + Send> = Box::new(next);
        Arc::get_mut(&mut self.0).unwrap().push_back(handler);
    }

    async fn compensate<'a>(
        evt: &'a E,
        list: LinkedList<&'a Box<dyn Handler<E, Err> + Send + Sync>>,
    ) {
        for handler in list.iter() {
            handler.compensate(evt).await.ok();
        }
    }
}

#[async_trait]
impl<E, Err> Publisher<E, Err> for GenericPublisherNode<E, Err>
where
    E: 'static + Send + Sync,
    Err: 'static + Sync + Send
{
    async fn publish(&self, evt: E) -> Result<(), Err> {
        debug!(target: "publisher", "[HANDLER] Handling event");

        let mut list = LinkedList::new();
        for handler in self.0.iter() {
            let result = handler.as_ref().handle(&evt).await;

            if result.is_err() {
                debug!(target: "publisher", "[COMPENSATION] Compensating event");

                GenericPublisherNode::<E, Err>::compensate(&evt, list).await;
                return result;
            } else {
                list.push_back(handler);
            }
        }

        Ok(())
    }
}
