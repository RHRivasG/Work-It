import {CreateRoutineCommand} from "application/commands/fitness/routines/CreateRoutineCommand";
import {CreateTrainingCommand} from "application/commands/fitness/trainings/CreateTrainingCommand";
import {MongoRoutineRepository} from "../repositories/fitness/routine.repository"
import {Router} from "express";
import {routineService} from "../services/routine.service";

const RoutineController = Router()

/*
RoutineController.get("/routines",(req, res) => {
	req.emitEvent(new CreateTrainingCommand("test","description test","1",[]))
	res.json("json test")
})
*/

const repository = new MongoRoutineRepository();

RoutineController.route('/routines')
	.post(async (req: any, res:any) => {
		const { body: {name, order, userId, trainings, description} } = req,
			createCommand = new CreateRoutineCommand(
				name,
				order,
				userId,
				trainings,
				description
			)
		let routine = await routineService.handle(createCommand) 
		res.json({ message: 'Ok', routine})
	})

export default RoutineController
