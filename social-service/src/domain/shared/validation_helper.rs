const SPECIAL_CHARS: &[char] = &[
    '/', '*', '&', '\\', '|', '°', '.', ',', ':', ';', '-', '+', '!', '?', '(', ')', '[', ']', '#',
    '@', '{', '}', '^', '\'', '"', '¿', '!', '$',
];

pub fn validate_password<A: AsRef<str>>(password: A) -> bool {
    let password = password.as_ref();
    let password_length_is_valid = password.len() >= 8;
    let password_has_lowercase = password.matches(char::is_lowercase).next().is_some();
    let password_has_uppercase = password.matches(char::is_uppercase).next().is_some();
    let password_has_ascii_digit = password
        .matches(|ch: char| ch.is_ascii_digit())
        .next()
        .is_some();
    let password_has_special_chars = password.matches(SPECIAL_CHARS).next().is_some();

    password_length_is_valid
        && password_has_lowercase
        && password_has_uppercase
        && password_has_ascii_digit
        && password_has_special_chars
}

pub fn validate_name<A: AsRef<str>>(name: A) -> bool {
    let name = name.as_ref();
    name.len() <= 50
}

pub fn validate_preferences<A: AsRef<str>, S: AsRef<[A]>>(preferences: S) -> bool {
    let preferences = preferences.as_ref();
    preferences.len() >= 3
}
