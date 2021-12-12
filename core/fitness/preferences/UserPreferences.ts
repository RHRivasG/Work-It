import { TrainingTaxonomy } from '../TrainingTaxonomy'

export class UserPreferences {
    constructor(
        private readonly userId: string,
        private readonly taxonomy: TrainingTaxonomy
    ) {}
}
