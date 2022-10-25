use mongodb::Collection;

use super::{participant::entity::ParticipantEntity, transformation_request::entity::RequestEntity, trainer::entity::TrainerEntity};

#[derive(Clone)]
pub(crate) struct MongodbOptions {
    pub participant_collection: Collection<ParticipantEntity>,
    pub request_collection: Collection<RequestEntity>,
    pub trainer_collection: Collection<TrainerEntity>
}

impl MongodbOptions {
    pub fn new(
        participant_collection: Collection<ParticipantEntity>,
        request_collection: Collection<RequestEntity>,
        trainer_collection: Collection<TrainerEntity>
    ) -> Self {
        MongodbOptions {
            participant_collection,
            request_collection,
            trainer_collection
        }
    }
}
