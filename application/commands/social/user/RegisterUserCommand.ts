import { TrainingTaxonomy } from "core/fitness/TrainingTaxonomy";

export class RegisterUserCommand {
    constructor(
        public name: string,
        public password: string,
        public preferences: TrainingTaxonomy[]
    ) { }
}
