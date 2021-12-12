import { TrainingTaxonomy } from "../../../fitness/TrainingTaxonomy";

export class TrainerRegisteredEvent {
    constructor(
        public name: string,
        public password: string,
        public preferences: Set<TrainingTaxonomy>
    ) { }
}
