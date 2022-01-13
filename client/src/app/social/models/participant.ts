import { RequestStatus } from "./request-status";

export type UnfinishedParticipant = Omit<Participant, "requesStatus">

export interface Participant {
  id: string,
  name: string,
  preferences: string[]
  requestStatus: RequestStatus
}
