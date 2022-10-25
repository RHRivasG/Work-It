use async_trait::async_trait;

#[async_trait]
pub trait Publisher<T, Err> {
    async fn publish(&self, evt: T) -> Result<(), Err>
    where
        T: 'async_trait;
    async fn publish_all(&self, evts: Vec<T>) -> Result<(), Err>
    where
        T: Send + 'async_trait,
    {
        for evt in evts {
            self.publish(evt).await?;
        }
        
        Ok(())
    }
}
