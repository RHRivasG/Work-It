import { User } from "core/social/users/user"

export interface UserRepository {
    get(id: string): Promise<User | undefined> | User | undefined
    delete(user: User): Promise<void> | void
    save(user: User): Promise<void> | void
    getAll(): Promise<User[]> | User[]
}
