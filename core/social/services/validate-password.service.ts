export interface ValidatePassword {
    validate(password: string, previousPassword: string): void
}
