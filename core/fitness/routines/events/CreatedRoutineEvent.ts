export class CreatedRoutineEvent {
	constructor(
		public id: string,
		public name: string = '',
		public order: number = 0,
		public userId: string = '',
		public trainings: string[] = [],
		public description: string = ''
	){}
}
