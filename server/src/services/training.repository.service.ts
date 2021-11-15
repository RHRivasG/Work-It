import { TrainingRepository } from "application/repositories/fitness/training.repository.interface";
import { TypeTrainingRepository } from "../repositories/fitness/training.repository";
import { connection } from "./connection.service";

export let trainingRepository: TrainingRepository

export const registerTrainingRepository = () => {
    trainingRepository = new TypeTrainingRepository(connection)
}
