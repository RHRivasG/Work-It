import { Schema } from "mongoose";
import { connection as authConnection } from "../../services/auth.connection.service";

export interface AuthorizationCodeDO {
    clientId: string,
    userId: string,
    code: string,
    redirectUrl: string
}

const AuthorizationCodeEntity = new Schema<AuthorizationCodeDO>({
    userId: String,
    clientId: String,
    code: String,
    redirectUrl: String
})

export const AuthorizationCodeModel = authConnection.model<AuthorizationCodeDO>("AuthorizationCode", AuthorizationCodeEntity)
