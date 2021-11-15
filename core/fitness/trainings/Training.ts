import { TrainingTaxonomy } from "../TrainingTaxonomy"
import { AggregateRoot } from "../../shared/AggregateRoot"
import { CreatedTrainingEvent } from "./events/CreatedTrainingEvent"
import { UpdatedTrainingEvent } from "./events/UpdatedTrainingEvent"
import { DeletedTrainingEvent } from "./events/DeletedTrainingEvent"
import { DuplicatedTaxonomiesError } from "./errors/DuplicatedTaxonomiesError"
import { TrainingVideo } from "./TrainingVideo"
import { TrainingVideoSetToTraining } from "./events/TrainingVideoSetToTraining"
import { DeletedTrainingVideoEvent } from "./events/DeletedTrainingVideoEvent"

type TrainingEvents = CreatedTrainingEvent
    | UpdatedTrainingEvent
    | DeletedTrainingEvent
    | TrainingVideoSetToTraining

type ExtractTrainingProperties = {
    -readonly [P in keyof Training]: Training[P] extends Function | undefined ? false : true
}

type TrainingProperties = Omit<{
    [P in keyof ExtractTrainingProperties as ExtractTrainingProperties[P] extends true ? P : never]: Training[P]
}, "id" | "trainingVideo" | "_id">

type UpdatedTrainingProperties = Partial<TrainingProperties>

export class Training extends AggregateRoot<TrainingEvents> {
    public categories: TrainingTaxonomy[] = []
    public trainerId: string = ''
    public name: string = ''
    public description: string = ''
    public trainingVideo: TrainingVideo | undefined = undefined

    static create(data: TrainingProperties) {
        const training = new Training(),
            { categories, name, trainerId, description } = data

        training.apply(
            new CreatedTrainingEvent(
                training.id,
                categories,
                trainerId,
                name,
                description
            )
        )
        return training
    }

    setVideo(filename: string, video: Buffer, ext?: string) {
        const fileSections = filename.split('.')
        this.apply(new TrainingVideoSetToTraining(
            this.id,
            filename,
            ext ?? fileSections[fileSections.length - 1],
            video
        ))
    }

    destroyVideo() {
        if (this.trainingVideo) {
            this.trainingVideo.bind(this.apply.bind(this)).destroy()
            this.trainingVideo = undefined
            return true
        }
    }

    update(data: UpdatedTrainingProperties) {
        this.apply(new UpdatedTrainingEvent(this.id, data))
    }

    destroy() {
        this.apply(new DeletedTrainingEvent(this.id))
    }

    protected when(event: TrainingEvents) {
        if (event instanceof CreatedTrainingEvent) Object.assign(this, event)
        if (event instanceof UpdatedTrainingEvent) Object.assign(this, event)
        if (event instanceof TrainingVideoSetToTraining) {
            if (this.trainingVideo) {
                this.trainingVideo.update(event.filename, event.video, event.ext)
            } else {
                const trainingVideo = TrainingVideo.create(
                    this.apply.bind(this),
                    event.filename,
                    event.video,
                    event.ext
                )
                this.trainingVideo = trainingVideo
            }
        }
        if (event instanceof DeletedTrainingEvent) this.destroyVideo()
        if (event instanceof DeletedTrainingVideoEvent) this.trainingVideo = undefined
    }

    protected invariants() {
        const duplicatedCategories = this.categories.flatMap(c => this.categories.filter(ct => c.value == ct.value))
        if (duplicatedCategories.length > 0)
            throw new DuplicatedTaxonomiesError(this.id, duplicatedCategories)
        if (!this.name || this.name.trim() == "")
            throw new Error("The name of the training cannot be blank")
        if (this.trainingVideo) {
            if (!this.trainingVideo.hasValidFilename()) throw Error('File name not valid')
            if (!this.trainingVideo.hasValidExt()) throw Error('File extension not valid')
            if (!this.trainingVideo.hasValidVideoSize()) throw Error('Video length not valid')
        }
    }
}
