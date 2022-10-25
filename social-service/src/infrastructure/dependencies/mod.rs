pub mod participant;
pub mod transformation_request;
pub mod trainer;
#[cfg(feature = "mongodb")]
pub mod mongodb; 

pub struct ApplicationState<P, TR, T> {
    participant_service: P,
    transformation_request_service: TR,
    trainer_service: T
}

impl<P, TR, T> ApplicationState<P, TR, T> {
    pub fn new(participant_service: P, transformation_request_service: TR, trainer_service: T) -> Self {
        ApplicationState {
            participant_service,
            transformation_request_service,
            trainer_service
        }
    }

    pub fn participant_service(&self) -> &P {
        &self.participant_service
    }

    pub fn transformation_request_service(&self) -> &TR {
        &self.transformation_request_service
    }

    pub fn trainer_service(&self) -> &T {
        &self.trainer_service
    }
}

impl<P, TR, T> Clone for ApplicationState<P, TR, T> 
where P: Clone,
      TR: Clone,
      T: Clone
{
    fn clone(&self) -> Self {
        ApplicationState { 
            participant_service: self.participant_service.clone(), 
            transformation_request_service: self.transformation_request_service.clone(),
            trainer_service: self.trainer_service.clone()
        }
    }
}
