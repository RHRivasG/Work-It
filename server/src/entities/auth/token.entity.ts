import { randomUUID } from "crypto";
import { Schema } from "mongoose";

export interface TokenDO {
    userId: string,
    clientId: string,
    token: string,
    scope: string[]
}

export const TokenEntity = new Schema<TokenDO>({
    userId: String,
    clientId: String,
    token: { type: String, default: randomUUID },
    scope: [String]
})
