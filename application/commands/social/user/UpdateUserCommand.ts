import { User } from "core/social/users/user";

export class UpdateUserCommand {
    constructor(
        public userId: string,
        public userData: Partial<User>
    ) { }
}
