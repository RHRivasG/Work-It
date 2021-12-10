import { Router } from "express";
import { RegisterUserCommand } from "application/commands/social/user/RegisterUserCommand";

const UserController = Router()

interface UserRegisterRequest {
    body: RegisterUserCommand
}

UserController.post("/user", (req: UserRegisterRequest, res) => {
})
