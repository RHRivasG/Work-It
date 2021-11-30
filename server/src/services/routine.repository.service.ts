import { RoutineRepository } from "application/repositories/fitness/routine.repository.interface"
import {  MongoRoutineRepository } from "../repositories/fitness/routine.repository"
import { connection } from "./connection.service"

export let routineRepository: RoutineRepository

export const registerRoutineRepository = () => {
	routineRepository = new MongoRoutineRepository(connection)
}
