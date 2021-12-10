export class RegisterAuthUserCommand {
    constructor(public username: string, public password: string, public normalUserId?: string, public trainerId?: string) { }
}
