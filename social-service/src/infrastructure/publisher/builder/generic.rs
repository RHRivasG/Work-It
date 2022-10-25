use frunk::{hlist, hlist::Selector, prelude::HList, HCons, HList};

use crate::infrastructure::publisher::{handler::Handler, node::GenericPublisherNode};

use super::{CanRegister, CanAdd, CanBuild};

pub struct GenericPublisherBuilder<L>(L);

impl Default for GenericPublisherBuilder<HList!(())> {
    fn default() -> Self {
        GenericPublisherBuilder(hlist![()])
    }
}

impl<H> GenericPublisherBuilder<H> {
    pub fn new<E, Err>(head: H) -> GenericPublisherBuilder<HList!(GenericPublisherNode<E, Err>)>
    where
        H: 'static + Handler<E, Err> + Send + Sync,
        E: 'static + Sync,
    {
        GenericPublisherBuilder(hlist![GenericPublisherNode::new(head)])
    }
}

impl<E, Err, H, L> CanRegister<E, Err, H> for GenericPublisherBuilder<L> 
where
    L: HList,
    H: 'static + Handler<E, Err> + Send + Sync,
    E: 'static + Sync,
{
    type Result = GenericPublisherBuilder<HCons<GenericPublisherNode<E, Err>, L>>;

    fn register_handler(self, handler: H) -> Self::Result
    {
        GenericPublisherBuilder(self.0.prepend(GenericPublisherNode::new(handler)))
    }
}

impl<E, Err, H, I, L> CanAdd<E, Err, H, I> for GenericPublisherBuilder<L>
where
    H: 'static + Handler<E, Err> + Send + Sync,
    E: 'static + Sync,
    L: Selector<GenericPublisherNode<E, Err>, I>,
{
    fn add_handler(&mut self, f: H) -> &mut Self {
        let node = self.0.get_mut();
        node.add(f);

        self
    }
}

impl<E, Err, I, L> CanBuild<E, Err, I> for GenericPublisherBuilder<L>
where
    L: Selector<GenericPublisherNode<E, Err>, I>,
    E: 'static + Sync + Send,
    Err: 'static + Sync + Send,
{
    type Publisher = GenericPublisherNode<E, Err>;

    fn build(&self) -> Self::Publisher {
        self.0.get().clone()
    }
}
