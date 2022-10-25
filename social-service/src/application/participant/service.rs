use async_trait::async_trait;

use crate::{
    application::traits::publisher::Publisher,
    domain::{
        participant::{events::ParticipantEvent, root::Participant},
        shared::uuid::UUID,
    },
};

use super::{
    dto::ParticipantDto,
    errors::ApplicationError,
    traits::{repository::Repository, use_case::UseCase},
};

pub struct ApplicationService<R, P> {
    repository: R,
    publisher: P,
}

impl<R, P> ApplicationService<R, P>
where
    R: Repository,
    P: Publisher<ParticipantEvent, ApplicationError>,
{
    pub fn new(repository: R, publisher: P) -> ApplicationService<R, P> {
        ApplicationService {
            repository,
            publisher,
        }
    }
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

#[async_trait]
impl<R, P> UseCase for ApplicationService<R, P>
where
    R: Repository + Send + Sync,
    P: Publisher<ParticipantEvent, ApplicationError> + Send + Sync,
{
    async fn delete(&self, id: UUID) -> Result<Option<()>, ApplicationError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant.delete();

            self.publisher
                .publish_all(participant.events().to_vec())
                .await?;

            return Ok(().into());
        }

        Ok(None)
    }

    async fn get(&self, id: UUID) -> Result<Option<ParticipantDto>, ApplicationError> {
        Ok(self.repository.find(id).await.map(Into::into))
    }

    async fn create<'a>(
        &self,
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<(), ApplicationError> {
        let participant = Participant::try_new(name, password, preferences)
            .map_err(ApplicationError::DomainError)?;

        self.publisher
            .publish_all(participant.events().to_vec())
            .await?;

        Ok(())
    }

    async fn update<'a>(
        &self,
        id: UUID,
        name: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Option<()>, ApplicationError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant
                .set_name(name)
                .map_err(ApplicationError::DomainError)?;
            participant
                .set_preferences(preferences)
                .map_err(ApplicationError::DomainError)?;

            self.publisher
                .publish_all(participant.events().to_vec())
                .await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn update_password<'a>(
        &self,
        id: UUID,
        password: &'a str
    ) -> Result<Option<()>, ApplicationError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant
                .set_password(password)
                .map_err(ApplicationError::DomainError)?;

            self.publisher.publish_all(participant.events().to_vec()).await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn request_transformation(&self, id: UUID) -> Result<Option<()>, ApplicationError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant.issue_transformation_request();

            self.publisher
                .publish_all(participant.events().to_vec())
                .await?;

            return Ok(().into());
        }

        return Ok(None);
    }

    async fn get_all(&self) -> Result<Vec<ParticipantDto>, ApplicationError> {
        let vec = self.repository.get_all().await;

        Ok(vec.into_iter().map(Into::into).collect())
    }
}

#[cfg(test)]
mod tests {
    use async_trait::async_trait;
    use futures::executor::block_on;
    use mockall::mock;

    use crate::{
        application::{
            participant::{
                errors::ApplicationError,
                traits::{repository::Repository, use_case::UseCase},
            },
            traits::publisher::Publisher,
        },
        domain::{
            participant::{events::ParticipantEvent, root::Participant},
            shared::uuid::UUID,
        },
    };

    use super::ApplicationService;

    mock! {
        Dummy {}

        #[async_trait]
        impl Publisher<ParticipantEvent, ApplicationError> for Dummy {
            async fn publish(&self, evt: ParticipantEvent) -> Result<(), ApplicationError>;
            async fn publish_all(&self, evts: Vec<ParticipantEvent>) -> Result<(), ApplicationError>;
        }

        #[async_trait]
        impl Repository for Dummy {
            async fn find(&self, id: UUID) -> Option<Participant>;
            async fn get_all(&self) -> Vec<Participant>;
        }
    }

    fn create_service() -> ApplicationService<MockDummy, MockDummy> {
        ApplicationService::new(MockDummy::new(), MockDummy::new())
    }

    #[test]
    fn emits_created_event() {
        let mut service = create_service();

        service
            .publisher
            .expect_publish_all()
            .times(1)
            .withf(|slice| {
                let first_event = slice.first().unwrap();

                if let ParticipantEvent::ParticipantCreated {
                    id: _,
                    name,
                    password,
                    preferences,
                } = first_event
                {
                    return slice.len() == 1
                        && name == "Michael Nelo"
                        && password == "KHearts358/2"
                        && preferences.to_vec() == vec!["legs", "arms", "body"];
                }

                false
            })
            .returning(|_| Ok(()));

        let result =
            block_on(service.create("Michael Nelo", "KHearts358/2", &["legs", "arms", "body"]));

        assert!(result.is_ok())
    }

    #[test]
    fn finds_participant() {
        let participant =
            Participant::try_new("Michael Nelo", "KHearts358/2", &["legs", "arms", "body"])
                .unwrap();
        let id: UUID = participant.id();
        let expected_id = id;

        let mut service = create_service();

        service
            .repository
            .expect_find()
            .times(1)
            .withf(move |pid| *pid == expected_id)
            .return_const(participant);

        let result = block_on(service.get(id)).unwrap().unwrap();

        assert_eq!(&*result.id, &*expected_id);
        assert_eq!(&*result.name, "Michael Nelo");
        assert_eq!(&*result.password, "KHearts358/2");
        assert_eq!(
            &*result.preferences,
            [
                "legs".to_string().into_boxed_str(),
                "arms".to_string().into_boxed_str(),
                "body".to_string().into_boxed_str()
            ]
        );
    }
}
