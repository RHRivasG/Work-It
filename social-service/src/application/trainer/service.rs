use async_trait::async_trait;

use crate::{application::traits::publisher::Publisher, domain::{trainer::{events::TrainerEvent, root::Trainer}, shared::uuid::UUID}};

use super::{traits::{use_case::UseCase, repository::Repository}, errors::ApplicationError, dto::TrainerDto};

pub struct ApplicationService<R, P> {
    repository: R,
    publisher: P,
}

impl<R, P> Clone for ApplicationService<R, P>
where
    R: Clone,
    P: Clone,
{
    fn clone(&self) -> Self {
        ApplicationService {
            repository: self.repository.clone(),
            publisher: self.publisher.clone(),
        }
    }
}

impl<R, P> ApplicationService<R, P> {
    pub fn new(repository: R, publisher: P) -> Self {
        ApplicationService { repository , publisher }
    }
}

#[async_trait]
impl<R, P> UseCase for ApplicationService<R, P> 
where R: Repository + Sync + Send,
      P: Publisher<TrainerEvent, ApplicationError> + Sync + Send
{
    async fn create<'a>(&self, id: UUID, name: &'a str, password: &'a str, preferences: &'a [&'a str]) -> Result<(), ApplicationError> {
        let trainer = Trainer::try_new_with_id(id, name, password, preferences).map_err(ApplicationError::DomainError)?;
        
        self.publisher.publish_all(trainer.events()).await?;

        Ok(()) 
    }

    async fn update<'a>(&self, id: UUID, name: &'a str, preferences: &'a [&'a str]) -> Result<Option<()>, ApplicationError> {
        let trainer_option = self.repository.get(id).await?;

        if let Some(mut trainer) = trainer_option {
            trainer.set_name(name).map_err(ApplicationError::DomainError)?;
            trainer.set_preferences(preferences).map_err(ApplicationError::DomainError)?;

            self.publisher.publish_all(trainer.events()).await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn update_password<'a>(&self, id: UUID, password: &'a str) -> Result<Option<()>, ApplicationError> {
        let trainer_option = self.repository.get(id).await?;

        if let Some(mut trainer) = trainer_option {
            trainer.set_password(password).map_err(ApplicationError::DomainError)?;

            self.publisher.publish_all(trainer.events()).await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn delete(&self, id: UUID) -> Result<Option<()>, ApplicationError> {
        let trainer_option = self.repository.get(id).await?;

        if let Some(mut trainer) = trainer_option {
            trainer.delete();

            self.publisher.publish_all(trainer.events()).await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn get(&self, id: UUID) -> Result<Option<TrainerDto>, ApplicationError> {
        let trainer_option = self.repository.get(id).await?;

        if let Some(trainer) = trainer_option {
            return Ok(Some(trainer.into()));
        }

        Ok(None)
    }

    async fn get_all(&self) -> Result<Vec<TrainerDto>, ApplicationError> {
        Ok(self.repository.get_all().await?.into_iter().map(|trainer| trainer.into()).collect())
    }
}
