import { TrainingTaxonomy } from "../TrainingTaxonomy";

export interface TrainingVideoDTO {
    name: string
    ext: string
    length: number
}

export interface TrainingDTO {
    categories: TrainingTaxonomy[]
    trainerId: string
    name: string
    description: string
    trainingVideo: TrainingVideoDTO
}
