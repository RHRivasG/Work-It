export class InvalidTrainerIdError extends Error {
    constructor(public trainerId: string) {
        super(`Trainer id is invalid: ${trainerId}`)
    }
}
