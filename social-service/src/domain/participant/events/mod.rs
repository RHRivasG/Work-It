type ImmutableUUID = Box<[u8]>;
type ImmutableString = Box<str>;
type ImmutablePreferences = Box<[ImmutableString]>;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ParticipantEvent {
    ParticipantCreated { id: ImmutableUUID, name: ImmutableString, password: ImmutableString, preferences: ImmutablePreferences },
    ParticipantUpdated { id: ImmutableUUID, name: ImmutableString, password: ImmutableString, preferences: ImmutablePreferences },
    ParticipantDeleted { id: ImmutableUUID },
    ParticipantTransformationRequestIssued { id: ImmutableUUID },
}
