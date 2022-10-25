use async_trait::async_trait;
use futures::Future;

#[async_trait]
pub trait Handler<E, Err> {
    async fn handle(&self, e: &E) -> Result<(), Err>;
    async fn compensate(&self, e: &E) -> Result<(), Err>;
}

pub struct FnHandler<F>(F);

pub fn fn_handler<E, Err, F, Fut>(f: F) -> FnHandler<F>
where
    F: Fn(&E) -> Fut + Sync + Send,
    Fut: Future<Output = Result<(), Err>> + Send,
    E: Sync,
{
    FnHandler(f)
}

#[async_trait]
impl<E, Err, F, Fut> Handler<E, Err> for FnHandler<F>
where
    F: Fn(&E) -> Fut + 'static + Sync + Send,
    Fut: Future<Output = Result<(), Err>> + 'static + Send,
    E: Sync + Send,
{
    async fn handle(&self, e: &E) -> Result<(), Err> {
        (self.0)(e).await
    }
    async fn compensate(&self, _: &E) -> Result<(), Err> {
        Ok(())
    }
}
