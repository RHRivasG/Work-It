use crate::application::traits::publisher::Publisher;

pub mod dynamic;
pub mod generic;

pub trait CanRegister<E, Err, H> {
    type Result;
    fn register_handler(self, handler: H) -> Self::Result;
}

pub trait CanAdd<E, Err, H, I> {
    fn add_handler(&mut self, handler: H) -> &mut Self;
}

pub trait CanBuild<E, Err, I> {
    type Publisher: Publisher<E, Err>;

    fn build(&self) -> Self::Publisher;
}
