export class ChangeAuthUsernameCommand {
    constructor(public username: string, public trainerId?: string, public normalId?: string) { }
}
