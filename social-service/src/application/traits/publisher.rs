use async_trait::async_trait;

#[async_trait]
pub trait Publisher<T> {
    async fn publish(&self, evt: T)
    where
        T: 'async_trait;
    async fn publish_all(&self, evts: Vec<T>)
    where
        T: Send + 'async_trait,
    {
        for evt in evts {
            self.publish(evt).await;
        }
    }
}
