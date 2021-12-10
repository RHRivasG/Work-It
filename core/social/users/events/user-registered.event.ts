import { TrainingTaxonomy } from "../../../fitness/TrainingTaxonomy";

export class UserRegisteredEvent {
    constructor(
        public name: string,
        public password: string,
        public preferences: Set<TrainingTaxonomy>
    ) { }
}
