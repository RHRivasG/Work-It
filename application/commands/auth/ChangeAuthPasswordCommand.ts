export class ChangeAuthPasswordCommand {
    constructor(public password: string, public trainerId?: string, public normalId?: string) { }
}
