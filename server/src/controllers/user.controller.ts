import { Request, Router } from "express";
import { RegisterUserCommand } from "application/commands/social/user/RegisterUserCommand";
import { userService } from "../services/user.service";
import { UpdateUserCommand } from "application/commands/social/user/UpdateUserCommand";
import { RegisterAuthUserCommand } from "application/commands/auth/RegisterAuthUserCommand";
import { ChangeAuthUsernameCommand } from "application/commands/auth/ChangeAuthUsernameCommand";
import { userRepository } from "../services/user.repository.service";
import { eventBus } from "../services/event-bus.service";

const UserController = Router()

interface UserRegisterRequest extends Request {
    body: RegisterUserCommand
}

interface UserUpdateRequest extends Request {
    body: UpdateUserCommand["userData"]
}

UserController.post("/user", async (req: UserRegisterRequest, res) => {
    const command = req.body,
        user = await userService.handle(command)

    eventBus.publish(new RegisterAuthUserCommand(
        user.name,
        user.password,
        user.id
    ))

    res.json({ msg: 'Successfully created user' })
})

UserController.route("/user/:id")
    .put(async (req: UserUpdateRequest, res) => {
        const userData = req.body
        await userService.handle(new UpdateUserCommand(req.params.id, userData))

        if (userData.name)
            eventBus.publish(new ChangeAuthUsernameCommand(userData.name, req.params.id))

        res.json({ msg: 'Successfully created user' })
    })
    .get(async (req, res) => {
        const user = await userRepository.get(req.params.id)

        if (!user) res.status(404).json({ msg: `User with id: ${req.params.id} not found` })
        else res.json(user)
    })
