import { Training } from "core/fitness/trainings/Training";
import { TrainingTaxonomy } from "core/fitness/TrainingTaxonomy";
import { model, ObjectId, Schema } from "mongoose";

export interface TrainingDO {
    id: string
    name: string
    description: string
    trainerId: string
    categories: TrainingTaxonomy[]
    trainingVideo: ObjectId
    asClassObject: () => Training
}

const TrainingTaxonomyEntity = new Schema<TrainingTaxonomy>({
    value: { type: String }
})

export const TrainingEntity = new Schema<TrainingDO>({
    id: { type: String, unique: true },
    name: String,
    description: String,
    trainerId: String,
    categories: [TrainingTaxonomyEntity],
    trainingVideo: { ref: 'TrainingVideo', type: Schema.Types.ObjectId },
}, { id: false })

TrainingEntity.method('asClassObject', function() {
    const training = new Training()
    training.id = this.id
    training.name = this.name
    training.description = this.description
    training.trainerId = this.trainerId
    training.categories = this.categories
    training.trainingVideo = this.trainingVideo?.asClassObject()

    return training
})

export const TrainingModel = model<TrainingDO>("Training", TrainingEntity)
