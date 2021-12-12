import { RequestHandler } from "express";

interface AuthSession {
    client_id: any,
    client_secret: any,
    redirect_uri: any,
    token: any
}

declare module "express-session" {
    interface SessionData {
        authInfo: Partial<AuthSession>
    }
}

export const storeCredentials = (...scopes: string[]): RequestHandler => (req, _, next) => {
    let clientId = req.query.client_id,
        clientSecret = req.query.secret,
        redirectUrl = req.query.redirect_uri

    req.session.authInfo = {}
    req.session.authInfo.client_id = clientId
    req.session.authInfo.client_secret = clientSecret
    req.session.authInfo.redirect_uri = redirectUrl
    req.query.scope = scopes.join(" ")
    req.body.scope = scopes.join(" ")

    next()
}

export const retriveCredentialsInBody: RequestHandler = (req, _, next) => {
    if (!req.query.code || !req.session.authInfo) return next()

    let clientId = req.session.authInfo.client_id,
        clientSecret = req.session.authInfo.client_secret,
        redirectUrl = req.session.authInfo.redirect_uri

    req.body.client_id = clientId
    req.body.client_secret = clientSecret
    req.body.redirect_uri = redirectUrl
    req.body.code = req.query.code
    req.body.grant_type = "authorization_code"

    next()
}

export const storeToken: RequestHandler = (req, res, next) => {
    const end = res.end
    res.end = (body) => {
        req.session.authInfo = req.session.authInfo || {}
        req.session.authInfo.token = JSON.parse(body).access_token
        res.end = end
        res.end(JSON.stringify({ msg: 'Login Complete' }))
    }
    next()
}

export const guard = (): RequestHandler => (req, res, next) => {
    if (!req.session.authInfo) return res.status(403).send('Unauthorized')
    if (!req.session.authInfo.token) return res.status(403).send('Unauthorized')

    req.headers.authorization = `Bearer ${req.session.authInfo.token}`
    next()
}

export const authorizeScopes = (...scopes: string[]): RequestHandler => (req, res, next) => {
    if (!req.authInfo) return res.status(403).send('Unauthorized')
    const acceptedScopes = req.authInfo.token.scope
    if (scopes.includes("*")) return next()
    if (!scopes.every(scope => acceptedScopes.includes(scope)))
        return res.status(403).send('Unauthorized')

    next()
}
