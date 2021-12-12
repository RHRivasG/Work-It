import { TrainingVideo } from "../TrainingVideo";

export class UpdatedTrainingVideoEvent {
    constructor(
        public readonly id: string,
        public readonly data: Partial<TrainingVideo & { video: Buffer }>
    ) { }
}
