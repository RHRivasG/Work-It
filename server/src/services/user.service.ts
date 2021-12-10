import { UserService } from "application/services/user/user.service"
import { userRepository } from "./user.repository.service";

export let userService: UserService;

export const registerUserService = () => {
    userService = new UserService(userRepository)
}
