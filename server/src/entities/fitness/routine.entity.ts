import { Routine } from "core/fitness/routines/Routine"
import { model, Schema } from "mongoose"

export interface RoutineDO {
	id: string
	name: string
	order: number
	userId: string
	trainings: string[]
	description?: string
	asClassObject(): Routine
}

export const RoutineEntity = new Schema<RoutineDO>({
	id: {type: String, unique: true },
	name: String,
	order: Number,
	userId: String,
	trainings: [String],
	description: String,
},{ id: false })

RoutineEntity.method('asClassObject', function(){
	const routine = new Routine()
	routine.id = this.id
	routine.name = this.name
	routine.order = this.order
	routine.userId = this.userId
	routine.trainings = this.trainings
	routine.description = this.description

	return routine
})

export const RoutineModel = model<RoutineDO>("Routine", RoutineEntity)
