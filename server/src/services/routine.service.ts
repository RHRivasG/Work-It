import { RoutineService } from "application/services/routine/routine.service"
import { routineRepository } from "./routine.repository.service"

export let routineService: RoutineService

export const registerRoutineRepository = () => {
	routineService = new RoutineService(routineRepository)
}
