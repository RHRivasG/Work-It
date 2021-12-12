import { Schema } from "mongoose";

export interface ClientDO {
    clientId: string,
    redirectUrl: string,
    secret: string
    isTrusted: boolean
}

export const ClientEntity = new Schema<ClientDO>({
    clientId: String,
    redirectUrl: String,
    isTrusted: { type: Boolean, default: () => false },
    secret: String
})
