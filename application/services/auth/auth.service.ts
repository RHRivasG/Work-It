import { ChangeAuthPasswordCommand } from "../../commands/auth/ChangeAuthPasswordCommand";
import { RegisterAuthUserCommand } from "../../commands/auth/RegisterAuthUserCommand";
import { RemoveAuthUserCommand } from "../../commands/auth/RemoveAuthUserCommand";
import { UserRepository } from "../../repositories/auth/user.repository.interface";
import { ApplicationService } from "../application.service";

type Commands =
    RegisterAuthUserCommand
    | ChangeAuthPasswordCommand
    | RemoveAuthUserCommand

export class AuthService implements
    ApplicationService<RegisterAuthUserCommand, void>,
    ApplicationService<ChangeAuthPasswordCommand, void>,
    ApplicationService<RemoveAuthUserCommand, void>
{

    constructor(private repository: UserRepository) { }

    handle(command: RegisterAuthUserCommand): Promise<undefined>
    handle(command: ChangeAuthPasswordCommand): Promise<undefined>
    handle(command: RemoveAuthUserCommand): Promise<undefined>
    async handle(command: Commands) {
        if (command instanceof RegisterAuthUserCommand) {
            const authInfo = {
                trainerId: command.trainerId,
                userId: command.normalUserId,
                name: command.username,
                password: command.password
            }
            await this.repository.save(authInfo)
        } else if (command instanceof ChangeAuthPasswordCommand) {
            const authInfo = await this.repository.get(command.normalId || command.trainerId)
            if (authInfo) {
                authInfo.password = command.password
                await this.repository.save(authInfo)
            }
        } else if (command instanceof RemoveAuthUserCommand) {
            const authInfo = await this.repository.get(command.trainerId || command.normalId)
            await this.repository.delete(authInfo)
        }
    }
}
