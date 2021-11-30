import { Routine } from "core/fitness/routines/Routine"

export interface RoutineRepository {
	save(routine: Routine): Promise<Routine>
}
