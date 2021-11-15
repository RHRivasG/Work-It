import { Training } from "../Training";

export class UpdatedTrainingEvent {
    constructor(
        public trainingId?: string,
        public data?: Partial<Training>
    ) { }
}
