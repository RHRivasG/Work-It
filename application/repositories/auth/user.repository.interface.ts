type AuthInfo = {
    trainerId?: string
    userId?: string
    name: string
    password: string
}

export interface UserRepository {
    save(user: AuthInfo): Promise<void> | void
    get(id?: string): Promise<AuthInfo> | AuthInfo
    delete(user: AuthInfo): Promise<void> | void
}
