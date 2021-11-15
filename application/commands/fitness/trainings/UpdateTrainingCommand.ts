import { Training } from "core/fitness/trainings/Training";

export class UpdateTrainingCommand {
    constructor(
        public id: string,
        public data: Partial<Training>
    ) { }
}
