import { TrainingTaxonomy } from "../../TrainingTaxonomy";

export class DuplicatedTaxonomiesError extends Error {
    constructor(public trainingId: string, public categories: TrainingTaxonomy[]) {
        super(`Taxonomies: ${categories.map(v => v.value).join(', ')}, repeated on training: ${trainingId} `)
    }
}
