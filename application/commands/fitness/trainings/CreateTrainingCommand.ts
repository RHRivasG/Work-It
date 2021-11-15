import { TrainingTaxonomy } from "core/fitness/TrainingTaxonomy";

export class CreateTrainingCommand {
    constructor(
        public readonly name: string,
        public readonly description: string,
        public readonly trainerId: string,
        public readonly categories: TrainingTaxonomy[]
    ) { }
}
