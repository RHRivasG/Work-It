import { TrainingTaxonomy } from '../../TrainingTaxonomy'

export class CreatedTrainingEvent {
    constructor(
        public id: string,
        public categories: TrainingTaxonomy[],
        public trainerId: string,
        public name: string,
        public description: string
    ) { }
}
