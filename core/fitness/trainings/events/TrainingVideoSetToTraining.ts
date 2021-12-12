export class TrainingVideoSetToTraining {
    constructor(
        public readonly id: string,
        public readonly filename: string,
        public readonly ext: string,
        public readonly video: Buffer
    ) { }
}
