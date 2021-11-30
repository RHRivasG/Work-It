import { TrainingVideo } from "core/fitness/trainings/TrainingVideo";
import { Schema } from "mongoose";
import { connection as fitnessConnection } from "../../services/fitness.connection.service";

export interface TrainingVideoDO {
    id: string
    name: string
    ext: string
    length: number
    asClassObject: () => TrainingVideo
}

export const TrainingVideoEntity = new Schema<TrainingVideoDO>({
    id: { type: String, unique: true },
    name: { type: String },
    ext: { type: String },
    length: { type: Number },
}, { id: false })

TrainingVideoEntity.method('asClassObject', function() {
    const trainingVideo = new TrainingVideo()
    trainingVideo.id = this.id
    trainingVideo.name = this.name
    trainingVideo.ext = this.ext
    trainingVideo.length = this.length

    return trainingVideo
})
