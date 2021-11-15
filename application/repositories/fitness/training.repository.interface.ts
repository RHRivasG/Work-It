import { Training } from "core/fitness/trainings/Training";

export interface TrainingRepository {
    save(training: Training): Promise<Training>
    find(id: string): Promise<Training | undefined>
    delete(training: Training): Promise<true | false>
    getAll(): Promise<Training[]>

    addVideo(training: Training): Promise<Training>
    addVideo(training: Training, buffer: Buffer): Promise<Training>
    deleteVideo(training: Training): Promise<Training>
    getVideo(training: Training): Promise<Buffer | undefined>
}
