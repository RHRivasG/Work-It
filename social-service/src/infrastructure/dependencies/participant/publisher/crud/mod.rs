#[cfg(feature = "mongodb")]
pub mod mongodb;

#[cfg(feature = "mongodb")]
pub use self::mongodb::{CrudHandler, create_crud_handler};
