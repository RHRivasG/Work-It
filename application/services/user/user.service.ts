import { ValidatePassword } from "core/social/services/validate-password.service";
import { User } from "core/social/users/user";
import { ChangeUserPasswordCommand } from "../../commands/social/user/ChangeUserPasswordCommand";
import { RegisterUserCommand } from "../../commands/social/user/RegisterUserCommand";
import { RemoveUserCommand } from "../../commands/social/user/RemoveUserCommand";
import { UpdateUserCommand } from "../../commands/social/user/UpdateUserCommand";
import { UserRepository } from "../../repositories/social/user.repository.interface";
import { ApplicationService } from "../application.service";

type Commands =
    RegisterUserCommand
    | ChangeUserPasswordCommand
    | UpdateUserCommand
    | RemoveUserCommand

export class UserService implements
    ApplicationService<RegisterUserCommand, User>,
    ApplicationService<ChangeUserPasswordCommand, void>,
    ApplicationService<RemoveUserCommand, void>,
    ApplicationService<UpdateUserCommand, void>
{
    constructor(private repo: UserRepository) { }

    private static PASSWORD_VALIDATOR: ValidatePassword = {
        validate(password: string, oldPassword: string) {
            const conditions = [
                /[A-Z]+/g,
                /[a-z]+/g,
                /[().,;@&/*-+=&#$<>!?\\_"'\[\]]/g
            ]

            const matchesAllConditions = conditions
                .map(r => Array.from(password.matchAll(r)))
                .every(r => r.length >= 1)

            if (password != oldPassword && matchesAllConditions) return

            throw new Error('Password does not fulfill all conditions')
        }
    }

    handle(command: RegisterUserCommand): Promise<User>
    handle(command: ChangeUserPasswordCommand): Promise<undefined>
    handle(command: RemoveUserCommand): Promise<undefined>
    handle(command: UpdateUserCommand): Promise<undefined>
    async handle(command: Commands) {
        if (command instanceof RegisterUserCommand) {
            const user = User.create(
                command.name,
                command.password,
                new Set(command.preferences),
                UserService.PASSWORD_VALIDATOR
            )

            await this.repo.save(user)
            return user
        } else if (command instanceof ChangeUserPasswordCommand) {
            const user = await this.repo.get(command.id)
            if (user) {
                user.changePassword(UserService.PASSWORD_VALIDATOR, command.password)
                await this.repo.save(user)
            }
        } else if (command instanceof UpdateUserCommand) {
            const user = await this.repo.get(command.userId)
            if (user) {
                user.update(command.userData)
                await this.repo.save(user)
            }
        } else if (command instanceof RemoveUserCommand) {
            const user = await this.repo.get(command.id)
            if (user) await this.repo.delete(user)
        }
    }

}
