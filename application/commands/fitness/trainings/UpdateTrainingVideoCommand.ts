export class UpdateTrainingVideoCommand {
    constructor(
        public trainingId: string,
        public filename: string,
        public video?: string,
        public ext?: string
    ) {

    }
}
