import passport from "passport";
import { Strategy as LocalStrategy } from "passport-local"
import { BasicStrategy as BasicStrategy } from "passport-http"
import { Strategy as ClientPasswordStrategy } from "passport-oauth2-client-password"
import { Strategy as BearerStrategy } from "passport-http-bearer"
import { ClientModel } from "../entities/auth/client.entity";
import { TokenModel } from "../entities/auth/token.entity";
import { UserModel } from "../entities/auth/user.entity";

declare global {
    namespace Express {
        class User {
            id: string
        }
    }
}

passport.use(new LocalStrategy(
    async (username, password, done) => {
        try {
            const user = await UserModel.findOne({ username, password })
            if (!user) done(null, false)
            done(null, user)
        } catch (e: any) {
            done(e)
        }
    }
))

passport.serializeUser((user, done) => done(null, user.id))
passport.deserializeUser(async (userId: string, done) => {
    try {
        const user = await UserModel.findOne({ id: userId })
        if (!user) done(null, false)
        done(null, user)
    } catch (e: any) {
        done(e)
    }
})

const findClient = async (clientId: string, clientSecret: string, done: (err: any, client?: any) => void) => {
    try {
        const client = await ClientModel.findOne({ clientId, secret: clientSecret })
        if (!client) return done(null, false)
        return done(null, client)
    } catch (e: any) {
        done(e)
    }
}

passport.use(new BasicStrategy(findClient))
passport.use(new ClientPasswordStrategy(findClient))

passport.use(new BearerStrategy(async (accessToken, done) => {
    try {
        const token = await TokenModel.findOne({ token: accessToken })
        if (!token) return done(null, false)
        const user = await UserModel.findOne({ id: token.userId })
        const client = await ClientModel.findOne({ clientId: token.clientId })
        done(null, user || client)
    } catch (err: any) {
        done(err)
    }
}))
