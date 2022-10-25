use std::fmt::{Write, Debug};
use std::{convert::TryFrom, ops::Deref};

use rand::{thread_rng, Rng};

use super::errors::UUIDError;

#[derive(PartialEq, Eq, Clone, Copy)]
pub struct UUID([u8; 16]);

impl UUID {
    pub fn new() -> Self {
        let mut rng = thread_rng();
        let mut buffer = [0; 16];

        (1..16).for_each(|i| {
            buffer[i] = rng.gen();
        });

        UUID(buffer)
    }
}

impl Default for UUID {
    fn default() -> Self {
        Self::new()
    }
}

impl Deref for UUID {
    type Target = [u8; 16];

    fn deref(&self) -> &[u8; 16] {
        &self.0
    }
}

impl ToString for UUID {
    fn to_string(&self) -> String {
        let mut string = String::with_capacity(32);

        for b in self.0 {
            write!(string, "{:02X}", b).unwrap();
        }

        string
    }
}

impl<'a> TryFrom<&'a str> for UUID {
    type Error = UUIDError;

    fn try_from(value: &'a str) -> Result<Self, Self::Error> {
        let mut buffer = [0; 16];
        let mut hyphens = 0;
        let mut hyphen_index = 0;

        if !(32..=36).contains(&value.len()) {
            return Err(UUIDError::StrFormatError);
        }

        for (i, c) in value.chars().enumerate() {
            if (i - hyphen_index) % 2 == 1 {
                continue;
            }

            if c == '-' {
                hyphens += 1;
                hyphen_index = i + 1;
                continue;
            }

            let byte: u8 =
                u8::from_str_radix(&value[i..=i + 1], 16).map_err(|_| UUIDError::StrFormatError)?;
            let buf_index = (i - hyphens) / 2;

            buffer[buf_index] = byte;
        }

        Ok(UUID(buffer))
    }
}


impl<'a> TryFrom<&'a [u8]> for UUID {
    type Error = UUIDError;

    fn try_from(value: &'a [u8]) -> Result<Self, Self::Error> {
        let mut buffer = [0; 16];

        if value.len() != 16 {
            return Err(UUIDError::StrFormatError);
        }

        buffer[..16].copy_from_slice(&value[..16]);

        Ok(UUID(buffer))
    }
}

impl From<[u8; 16]> for UUID {
    fn from(buf: [u8; 16]) -> Self {
        UUID(buf)
    }
}

impl Debug for UUID {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        Debug::fmt(&self.to_string(), f)
    }
}

#[cfg(test)]
mod tests {
    use crate::domain::shared::uuid::UUID;

    #[test]
    fn parses_compliant_4dashes_uuid_correctly() {
        let uuid_str = "971f2a79-a086-46b0-9c9c-dbc2fc74aff6";
        let uuid_result: Result<UUID, _> = uuid_str.try_into();
        let expected_uuid = UUID::from([
            0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
            0xaf, 0xf6,
        ]);

        assert!(uuid_result.is_ok());
        assert_eq!(Ok(expected_uuid), uuid_result);
    }

    #[test]
    fn parses_compliant_3dashes_uuid_correctly() {
        let uuid_str = "971f2a79a086-46b0-9c9c-dbc2fc74aff6";
        let uuid_result: Result<UUID, _> = uuid_str.try_into();
        let expected_uuid = UUID::from([
            0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
            0xaf, 0xf6,
        ]);

        assert!(uuid_result.is_ok());
        assert_eq!(Ok(expected_uuid), uuid_result);
    }

    #[test]
    fn parses_compliant_2dashes_uuid_correctly() {
        let uuid_str = "971f2a79a08646b0-9c9c-dbc2fc74aff6";
        let uuid_result: Result<UUID, _> = uuid_str.try_into();
        let expected_uuid = UUID::from([
            0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
            0xaf, 0xf6,
        ]);

        assert!(uuid_result.is_ok());
        assert_eq!(Ok(expected_uuid), uuid_result);
    }

    #[test]
    fn parses_compliant_1dashes_uuid_correctly() {
        let uuid_str = "971f2a79a08646b09c9c-dbc2fc74aff6";
        let uuid_result: Result<UUID, _> = uuid_str.try_into();
        let expected_uuid = UUID::from([
            0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
            0xaf, 0xf6,
        ]);

        assert!(uuid_result.is_ok());
        assert_eq!(Ok(expected_uuid), uuid_result);
    }

    #[test]
    fn parses_compliant_0dashes_uuid_correctly() {
        let uuid_str = "971f2a79a08646b09c9cdbc2fc74aff6";
        let uuid_result: Result<UUID, _> = uuid_str.try_into();
        let expected_uuid = UUID::from([
            0x97, 0x1f, 0x2a, 0x79, 0xa0, 0x86, 0x46, 0xb0, 0x9c, 0x9c, 0xdb, 0xc2, 0xfc, 0x74,
            0xaf, 0xf6,
        ]);

        assert!(uuid_result.is_ok());
        assert_eq!(Ok(expected_uuid), uuid_result);
    }
}
