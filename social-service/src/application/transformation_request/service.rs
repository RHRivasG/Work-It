use async_trait::async_trait;

use crate::{
    application::traits::publisher::Publisher,
    domain::{
        shared::uuid::UUID,
        transformation_request::{events::TransformationRequestEvent, root::Request},
    },
};

use super::{
    errors::ApplicationError,
    traits::{repository::Repository, use_case::UseCase},
};

pub struct ApplicationService<R, P> {
    repository: R,
    publisher: P,
}

impl<R, P> ApplicationService<R, P> {
    pub fn new(repository: R, publisher: P) -> Self {
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
    R: Repository + Sync + Send,
    P: Publisher<TransformationRequestEvent, ApplicationError> + Sync + Send,
{
    async fn create_transformation_request(&self, id: UUID) -> Result<(), ApplicationError> {
        let request = Request::new(id);

        self.publisher
            .publish_all(request.events().to_vec())
            .await?;

        Ok(())
    }

    async fn accept_transformation_request(
        &self,
        id: UUID,
    ) -> Result<Option<()>, ApplicationError> {
        let request_option = self.repository.find(id).await?;

        if let Some(mut request) = request_option {
            request.accept().map_err(ApplicationError::DomainError)?;

            self.publisher
                .publish_all(request.events().to_vec())
                .await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn reject_transformation_request(
        &self,
        id: UUID,
    ) -> Result<Option<()>, ApplicationError> {
        let request_option = self.repository.find(id).await?;

        if let Some(mut request) = request_option {
            request.reject().map_err(ApplicationError::DomainError)?;

            self.publisher
                .publish_all(request.events().to_vec())
                .await?;

            return Ok(Some(()));
        }

        Ok(None)
    }

    async fn delete_transformation_request(&self, id: UUID) -> Result<Option<()>, ApplicationError> {
        let request_option = self.repository.find(id).await?;

        if let Some(mut request) = request_option {
            request.delete();

            self.publisher
                .publish_all(request.events().to_vec())
                .await?;

            return Ok(Some(()));
        }

        Ok(None)
    }
}
