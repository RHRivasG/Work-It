import { Training } from "./training";

export interface Routine {
  id: string
  userId: string
  name: string
  description: string,
  trainings: string[]
}

export type FullRoutine = {
  [key in keyof Routine]: key extends "trainings" ? Training[] : Routine[key]
}
