import { randomUUID } from "crypto";
import { Schema } from "mongoose";
import { connection as authConnection } from "../../services/auth.connection.service";

export interface UserDO {
    id: string
    username: string,
    password: string,
    trainerId?: string
    normalId?: string
}

const UserEntity = new Schema<UserDO>({
    id: { type: String, default: randomUUID },
    username: String,
    password: String,
    trainerId: String,
    normalId: String
})

export const UserModel = authConnection.model<UserDO>("User", UserEntity)
