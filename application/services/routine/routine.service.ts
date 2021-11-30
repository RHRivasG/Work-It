import {Routine} from "core/fitness/routines/Routine";
import {CreateRoutineCommand} from "../../commands/fitness/routines/CreateRoutineCommand";
import {RoutineRepository} from "../../repositories/fitness/routine.repository.interface";
import {ApplicationService} from "../application.service";

type Commands = CreateRoutineCommand

export class RoutineService implements ApplicationService<CreateRoutineCommand, Routine> {

	constructor(private repository: RoutineRepository){}

	handle(command: CreateRoutineCommand): Promise<Routine>
	async handle(command: Commands){
		if (command instanceof CreateRoutineCommand) {
			const routine = Routine.create({
				name: command.name,
				order: command.order,
				userId: command.userId,
				trainings: command.trainings,
				description: command.description
			})
			await this.repository.save(routine)
			return routine
		}
	}

}
