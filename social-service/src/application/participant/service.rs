use async_trait::async_trait;

use crate::{
    application::traits::publisher::Publisher,
    domain::participant::{errors::ParticipantError, events::ParticipantEvent, root::Participant},
};

use super::{
    dto::ParticipantDto,
    traits::{repository::Repository, use_case::UseCase},
};

pub struct ApplicationService<R, P> {
    repository: R,
    publisher: P,
}

impl<R, P> ApplicationService<R, P>
where
    R: Repository,
    P: Publisher<ParticipantEvent>,
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
    P: Clone
{
    fn clone(&self) -> Self {
        ApplicationService { 
            repository: self.repository.clone(), 
            publisher: self.publisher.clone() 
        }
    }
}

#[async_trait]
impl<R, P> UseCase for ApplicationService<R, P>
where
    R: Repository + Send + Sync,
    P: Publisher<ParticipantEvent> + Send + Sync,
{
    async fn delete(&self, id: &str) -> Result<Option<()>, ParticipantError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant.delete();

            self.publisher
                .publish_all(participant.events().to_vec())
                .await;

            return Ok(().into());
        }

        Ok(None)
    }

    async fn get(&self, id: &str) -> Option<ParticipantDto> {
        self.repository.find(id).await.map(Into::into)
    }

    async fn create<'a>(
        &self,
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<(), ParticipantError> {
        let participant = Participant::try_new(name, password, preferences)?;

        self.publisher
            .publish_all(participant.events().to_vec())
            .await;

        Ok(())
    }

    async fn update<'a>(
        &self,
        id: &'a str,
        name: &'a str,
        password: &'a str,
        preferences: &'a [&'a str],
    ) -> Result<Option<()>, ParticipantError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant.set_name(name)?;

            participant.set_password(password)?;

            participant.set_preferences(preferences)?;

            self.publisher
                .publish_all(participant.events().to_vec())
                .await;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn request_transformation(&self, id: &str) -> Result<Option<()>, ParticipantError> {
        let participant = self.repository.find(id).await;

        if let Some(mut participant) = participant {
            participant.issue_transformation_request();

            self.publisher
                .publish_all(participant.events().to_vec())
                .await;

            return Ok(().into())
        }

        return Ok(None)
    }

    async fn get_all(&self) -> Vec<ParticipantDto> {
        let vec = self.repository.get_all().await;

        vec.into_iter().map(Into::into).collect()
    }
}

#[cfg(test)]
mod tests {
    use async_trait::async_trait;
    use futures::executor::block_on;
    use mockall::mock;

    use crate::{
        application::{
            participant::traits::{repository::Repository, use_case::UseCase},
            traits::publisher::Publisher,
        },
        domain::participant::{events::ParticipantEvent, root::Participant},
    };

    use super::ApplicationService;

    mock! {
        Dummy {}

        #[async_trait]
        impl Publisher<ParticipantEvent> for Dummy {
            async fn publish(&self, evt: ParticipantEvent);
            async fn publish_all(&self, evts: Vec<ParticipantEvent>);
        }

        #[async_trait]
        impl Repository for Dummy {
            async fn find(&self, id: &str) -> Option<Participant>;
            async fn get_all(&self) -> Vec<Participant>;
        }
    }

    fn create_service(
        repository: MockDummy,
        publisher: MockDummy,
    ) -> ApplicationService<MockDummy, MockDummy> {
        ApplicationService::new(repository, publisher)
    }

    #[test]
    fn emits_created_event() {
        let mut publisher = MockDummy::new();
        let repo = MockDummy::new();

        publisher
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
                        && name.as_ref() == "Michael Nelo"
                        && password.as_ref() == "KHearts358/2"
                        && preferences.as_ref()
                            == [
                                "legs".to_string().into_boxed_str(),
                                "arms".to_string().into_boxed_str(),
                                "body".to_string().into_boxed_str(),
                            ];
                }

                false
            })
            .return_const(());

        let result = block_on(create_service(repo, publisher).create(
            "Michael Nelo",
            "KHearts358/2",
            &["legs", "arms", "body"],
        ));
        assert_eq!(result, Ok(()))
    }

    #[test]
    fn finds_participant() {
        let participant =
            Participant::try_new("Michael Nelo", "KHearts358/2", &["legs", "arms", "body"])
                .unwrap();
        let id = participant.id();
        let expected_id = id;
        let id = id.to_string();

        let publisher = MockDummy::new();
        let mut repo = MockDummy::new();

        repo.expect_find()
            .times(1)
            .withf(move |pid| pid == &*expected_id.to_string())
            .return_const(participant);

        let result = block_on(create_service(repo, publisher).get(&*id)).unwrap();

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
