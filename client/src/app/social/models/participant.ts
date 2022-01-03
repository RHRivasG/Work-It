import { RequestStatus } from "./request-status";

export interface Participant {
  id: string,
  name: string,
  preferences: string[]
  requestStatus: RequestStatus
}
