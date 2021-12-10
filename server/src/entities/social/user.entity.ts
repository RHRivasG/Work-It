import { TrainingTaxonomy } from "core/fitness/TrainingTaxonomy";
import { Schema } from "mongoose";

export interface UserDO {
    id: string
    username: string
    password: string
    trainingTaxonomy: TrainingTaxonomy[]
}

export const UserEntity = new Schema<UserDO>({
    id: String,
    username: String,
    password: String,
    trainingTaxonomy: [TrainingTaxonomy]
}, { id: false })
