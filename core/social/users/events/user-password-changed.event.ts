export class UserPasswordChangedEvent {
    constructor(public userId: string, public password: string) { }
}
