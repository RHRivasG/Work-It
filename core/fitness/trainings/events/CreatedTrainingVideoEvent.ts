export class CreatedTrainingVideoEvent {
    constructor(
        public readonly id: string,
        public readonly name: string,
        public readonly ext: string,
        public readonly video: Buffer
    ) { }
}
