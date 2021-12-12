export class CreateRoutineCommand {
	constructor(
		public readonly name: string,
		public readonly order: number,
		public readonly userId: string,
		public readonly trainings: string[],
		public readonly description: string
	){}
}
