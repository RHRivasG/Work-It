import {CreateTrainingCommand} from "application/commands/fitness/trainings/CreateTrainingCommand";
import {Router} from "express";

const RoutineController = Router()

RoutineController.get("/routines",(req, res) => {
	req.emitEvent(new CreateTrainingCommand("test","description test","1",[]))
	res.json("json test")
})

export default RoutineController
