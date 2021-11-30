import { randomUUID } from "crypto";
import { Schema } from "mongoose";
import { connection as authConnection } from "../../services/auth.connection.service";

export interface TokenDO {
    userId: string,
    clientId: string,
    token: string
}

const TokenEntity = new Schema<TokenDO>({
    userId: String,
    clientId: String,
    token: { type: String, default: randomUUID }
})

export const TokenModel = authConnection.model<TokenDO>("Token", TokenEntity)
