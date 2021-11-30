import {CreateRoutineCommand} from "application/commands/fitness/routines/CreateRoutineCommand";
import {EventBus} from "core/shared/EventBus";
import {EventHandlerBuilder} from "../middlewares/event-bus.middleware";
import {routineService} from "../services/routine.service";

const RoutineHandler = new EventHandlerBuilder()

RoutineHandler.handle((bus: EventBus)=>
	bus
	.forEvents(CreateRoutineCommand)
	.subscribe(e => routineService.handle(e))

)

export default RoutineHandler
