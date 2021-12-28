use ::redis::RedisError;
use std::{future::Future, pin::Pin};

pub mod redis;

pub trait TokenStore {
    fn remove<'a>(
        &'a self,
        key: String,
    ) -> Pin<Box<dyn Future<Output = Result<(), RedisError>> + Send>>;

    fn set<'a>(
        &'a self,
        key: String,
        token: String,
    ) -> Pin<Box<dyn Future<Output = Result<(), RedisError>> + Send>>;

    fn get<'a>(
        &'a self,
        key: String,
    ) -> Pin<Box<dyn Future<Output = Result<String, RedisError>> + Send>>;
}
