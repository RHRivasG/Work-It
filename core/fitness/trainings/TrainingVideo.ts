import { Entity } from "../../shared/Entity";
import { Publisher } from "../../shared/Publisher";
import { CreatedTrainingVideoEvent } from "./events/CreatedTrainingVideoEvent";
import { DeletedTrainingVideoEvent } from "./events/DeletedTrainingVideoEvent";
import { UpdatedTrainingVideoEvent } from "./events/UpdatedTrainingVideoEvent";

export type TrainingVideoEvents =
    CreatedTrainingVideoEvent
    | UpdatedTrainingVideoEvent
    | DeletedTrainingVideoEvent

export class TrainingVideo extends Entity<TrainingVideoEvents> {
    name: string = ''
    ext: string = ''
    length: number = 0

    static VALID_VIDEO_EXTENSIONS = [
        'mp4',
        'mp3',
        'mpeg'
    ]

    static create(
        pub: Publisher<TrainingVideoEvents>,
        filename: string,
        buffer: Buffer,
        ext: string | undefined = undefined
    ) {
        const fileSplit = filename.split('.'),
            fileExt = ext ?? fileSplit[fileSplit.length - 1],
            video = new TrainingVideo().bind(pub)

        video.apply(new CreatedTrainingVideoEvent(
            video.id,
            filename,
            fileExt,
            buffer
        ))

        return video
    }

    destroy() {
        this.apply(new DeletedTrainingVideoEvent(this.id))
    }

    update(filename: string, buffer: Buffer, ext: string | undefined = undefined) {
        const fileSplit = filename.split('.'),
            fileExts = ext ?? fileSplit[fileSplit.length - 1]

        this.apply(new UpdatedTrainingVideoEvent(this.id, {
            ext: fileExts,
            name: filename,
            length: buffer?.length ?? 0
        }))
    }

    hasValidExt() {
        return TrainingVideo.VALID_VIDEO_EXTENSIONS.includes(this.ext)
    }

    hasValidFilename() {
        return this.name != ''
    }

    hasValidVideoSize() {
        return this.length > 0
    }

    protected when(event: TrainingVideoEvents): void {
        if (event instanceof CreatedTrainingVideoEvent) {
            this.length = event.video.length
            this.name = event.name
            this.ext = event.ext
        }
        else if (event instanceof UpdatedTrainingVideoEvent)
            Object.assign(this, event.data)
    }
}
