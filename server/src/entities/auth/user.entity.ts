import { randomUUID } from "crypto";
import { Schema } from "mongoose";

export interface UserDO {
    id: string
    username: string,
    password: string,
    trainerId?: string
    normalId?: string
}

export const UserEntity = new Schema<UserDO>({
    id: { type: String, default: randomUUID },
    username: String,
    password: String,
    trainerId: String,
    normalId: String
})
