import {AggregateRoot} from "../../shared/AggregateRoot";
import {CreatedRoutineEvent} from "./events/CreatedRoutineEvent";

export class Routine extends AggregateRoot<any>{
	public name: string = ''
	public order: number = 0
	public userId: string = ''
	public trainings: string[] = []
	public description?: string = ''

	static create(data: any) {
		const routine = new Routine(),
			{ name, order, userId, trainings, description } = data

		routine.apply(
			new CreatedRoutineEvent(
				routine.id,
				name,
				order,
				userId,
				trainings,
				description
			)
		)

		return routine
	}

	/*

	addTraining(training: any){
		//TODO TrainingAddedToRoutine
		this.apply(event)
	}

	removeTraining(trainingId: string){
		//TODO TrainingRemovedFromRoutine
	}

	updateDetails(data: any){
		//TODO UpdatedRoutineDetailsEvent
	}

	updateOrder(){
		//TODO UpdatedRoutineOrderEvent
	}

	updateTrainigsOrder(){
		//TODO UpdatedTrainingOrderToRoutineEvent
	}

	destroy(){
	}
	*/

	protected when(event: any){
		if (event instanceof CreatedRoutineEvent){
			this.name = event.name
			this.order = event.order
			this.userId = event.userId
			this.trainings = event.trainings
			this.description = event.description
		}
	}

	protected invariants(){
	}
}
