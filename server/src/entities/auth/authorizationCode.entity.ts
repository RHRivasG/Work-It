import { Schema } from "mongoose";

export interface AuthorizationCodeDO {
    clientId: string,
    userId: string,
    code: string,
    redirectUrl: string,
    scope: string[]
}

export const AuthorizationCodeEntity = new Schema<AuthorizationCodeDO>({
    userId: String,
    clientId: String,
    code: String,
    redirectUrl: String,
    scope: [String]
})
