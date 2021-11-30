import { Schema } from "mongoose";
import { connection as authConnection } from "../../services/auth.connection.service";

export interface ClientDO {
    clientId: string,
    redirectUrl: string,
    secret: string
    isTrusted: boolean
}

const ClientEntity = new Schema<ClientDO>({
    clientId: String,
    redirectUrl: String,
    isTrusted: { type: Boolean, default: () => false },
    secret: String
})

export const ClientModel = authConnection.model<ClientDO>("Client", ClientEntity)
