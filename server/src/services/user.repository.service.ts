import { UserRepository } from "application/repositories/social/user.repository.interface";
import { MongoUserRepository } from "../repositories/social/user.repository";

export let userRepository: UserRepository;

export const registerUserRepository = () => {
    userRepository = new MongoUserRepository()
}
