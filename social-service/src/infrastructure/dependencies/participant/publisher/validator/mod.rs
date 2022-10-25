#[cfg(feature = "mongodb")]
pub mod mongodb;

#[cfg(feature = "mongodb")]
pub use self::mongodb::{ValidatorHandler, create_validator_handler};
