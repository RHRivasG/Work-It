import { TrainingTaxonomy } from "core/fitness/TrainingTaxonomy";
import { Schema } from "mongoose";

export const TrainingTaxonomyEntity = new Schema<TrainingTaxonomy>({
    value: { type: String }
})
