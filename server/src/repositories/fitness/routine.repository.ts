import { RoutineRepository } from "application/repositories/fitness/routine.repository.interface";
import { Routine } from "core/fitness/routines/Routine"
import { Connection } from "mongoose";
import { RoutineModel } from "../../services/entities.service";

export class MongoRoutineRepository implements RoutineRepository {
	constructor(private connection: Connection) { }

	async save(routine: Routine) {
		const model = new RoutineModel(routine)
		await model.save();
		return model.asClassObject()
	}
}

