import { RoutineService } from "application/services/routine/routine.service"
import { routineRepository } from "./routine.repository.service"

export let routineService: RoutineService

export const registerRoutineService = () => {
	routineService = new RoutineService(routineRepository)
}
