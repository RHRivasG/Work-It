import { Trainer } from "../trainer";

export class TrainerUpdatedEvent {
    constructor(public trainer: Partial<Trainer>) { }
}
