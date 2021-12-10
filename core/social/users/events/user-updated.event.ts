import { User } from "../user";

export class UserUpdatedEvent {
    constructor(public userId: string, public data: Partial<User>) { }
}
