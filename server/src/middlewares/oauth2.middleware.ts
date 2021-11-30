import { randomUUID } from "crypto";
import { createServer, exchange, grant } from "oauth2orize";
import { AuthorizationCodeModel } from "../entities/auth/authorizationCode.entity";
import { ClientDO, ClientModel } from "../entities/auth/client.entity";
import { TokenModel } from "../entities/auth/token.entity";
import { UserModel } from "../entities/auth/user.entity";

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

server.grant(grant.code(async (client, redirectUrl, user, _, done) => {
    try {
        let code = randomUUID()
        await AuthorizationCodeModel.create({
            clientId: client.clientId,
            userId: user.id,
            code,
            redirectUrl
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
            token
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
            userId: authCode.userId
        })

        done(null, token)
    } catch (err: any) {
        done(err)
    }
}))

server.exchange(exchange.password(async (client, username, password, scope, done) => {
    try {
        const verifyedClient = await ClientModel.findOne({ clientId: client.clientId, secret: client.secret })
        if (!verifyedClient) return done(null, false)
        const user = await UserModel.findOne({ username, password })
        if (!user) return done(null, false)
        const { token } = await TokenModel.create({
            clientId: client.id,
            userId: user.id,
        })

        done(null, token)
    } catch (err: any) {
        done(err)
    }
}))

server.exchange(exchange.clientCredentials(async (client, scope, done) => {
    try {
        const verifyedClient = await ClientModel.findOne({ clientId: client.clientId, secret: client.secret })
        if (!verifyedClient) return done(null, false)
        const { token } = await TokenModel.create({
            clientId: verifyedClient.clientId,
            userId: null,
        })

        done(null, token)
    } catch (e: any) {
        done(e)
    }
}))

export const authorize = server.authorization(async (clientId, redirectUrl, done) => {
    try {
        const client = await ClientModel.findOne({ clientId, redirectUrl })
        if (!client) return done(null, false)

        done(null, client, client.redirectUrl)
    } catch (err: any) {
        done(err)
    }
}, async (client, user, done: any) => {
    const token = await TokenModel.findOne({ clientId: client.id, userId: user.id })
    if (client.isTrusted) return done(null, true)
    if (token) return done(null, true)

    return done(null, false)
})

export const decision = server.decision()

export const token = server.token()
