use crate::domain::participant::root::Participant;

pub struct ParticipantDto {
    pub id: Box<[u8]>,
    pub name: Box<str>,
    pub password: Box<str>,
    pub preferences: Box<[Box<str>]>
}

impl From<Participant> for ParticipantDto {
    fn from(val: Participant) -> Self {
        let id = val.id().to_vec().into_boxed_slice();
        let preferences = val
            .preferences()
            .iter()
            .map(|tag| tag.to_string().into_boxed_str())
            .collect::<Vec<Box<str>>>()
            .into_boxed_slice();

        ParticipantDto {
            id,
            name: val.name().to_string().into_boxed_str(),
            password: val.password().to_string().into_boxed_str(),
            preferences
        }
    }
}
