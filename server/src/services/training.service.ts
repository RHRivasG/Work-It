import { TrainingService } from "application/services/training/training.service";
import { trainingRepository } from "./training.repository.service";

export let trainingService: TrainingService

export const registerTrainingService = () => {
    trainingService = new TrainingService(trainingRepository)
}
