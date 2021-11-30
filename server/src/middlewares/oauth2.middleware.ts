import { randomUUID } from "crypto";
import { createServer, exchange, grant } from "oauth2orize";
import { AuthorizationCodeModel, ClientModel, TokenModel } from "../services/entities.service";

let server = createServer()

server.serializeClient((client, done) => done(null, client.clientId))

server.deserializeClient(async (clientId, done) => {
    try {
        const client = await ClientModel.findOne({ clientId })
        done(null, client)
    } catch (err: any) {
        done(err)
    }
})

server.grant(grant.code(async (client, redirectUrl, user, ares, done) => {
    try {
        let code = randomUUID()
        await AuthorizationCodeModel.create({
            clientId: client.clientId,
            userId: user.id,
            code,
            redirectUrl,
            scope: ares.scope || ['*']
        })
        done(null, code)
    } catch (err: any) {
        done(err)
    }
}))

server.grant(grant.token(async (client, user, ares, done) => {
    try {
        const token = randomUUID()
        await TokenModel.create({
            userId: user.id,
            clientId: client.clientId,
            token,
            scope: ares.scope || ['*']
        })
    } catch (err: any) {
        done(err)
    }
}))

server.exchange(exchange.code(async (client, code, redirectUrl, done) => {
    try {
        let authCode = await AuthorizationCodeModel.findOne({ code })
        if (!authCode) return done(null, false)
        if (client.clientId != authCode.clientId) return done(null, false)
        if (redirectUrl != authCode.redirectUrl) return done(null, false)
        const { token } = await TokenModel.create({
            clientId: authCode.clientId,
            userId: authCode.userId,
            scope: authCode.scope
        })
        await authCode.delete()

        done(null, token)
    } catch (err: any) {
        done(err)
    }
}))

export const authorize = (scopes: string[]) => server.authorization(async (clientId, redirectUrl, done) => {
    try {
        const client = await ClientModel.findOne({ clientId, redirectUrl })
        if (!client) return done(null, false)

        done(null, client, client.redirectUrl)
    } catch (err: any) {
        done(err)
    }
}, async (client, user, done: any) => {
    try {
        const token = await TokenModel.findOne({ clientId: client.id, userId: user.id })
        if (client.isTrusted) return done(null, true)
        if (token && (scopes.length == 0 || scopes.every(scope => token.scope.includes(scope))))
            return done(null, true)

        return done(null, false)
    } catch (e: any) {
        done(e)
    }
})

export const decision = server.decision()

export const token = server.token()
