import passport from "passport";
import { Strategy as LocalStrategy } from "passport-local"
import { BasicStrategy as BasicStrategy } from "passport-http"
import { Strategy as ClientPasswordStrategy } from "passport-oauth2-client-password"
import { Strategy as BearerStrategy } from "passport-http-bearer"
import { AuthUserModel, ClientModel, TokenModel } from "../services/entities.service";
import { TokenDO } from "../entities/auth/token.entity";
import { Document } from "mongoose";

declare global {
    namespace Express {
        class User {
            id: string
        }
        interface AuthInfo {
            token: TokenDO & Document,
            user?: string
        }
    }
}

const findClient = async (clientId: string, clientSecret: string, done: (err: any, client?: any) => void) => {
    try {
        const client = await ClientModel.findOne({ clientId, secret: clientSecret })
        if (!client) return done(null, false)
        return done(null, client)
    } catch (e: any) {
        done(e)
    }
}

passport.use(new LocalStrategy(
    async (username, password, done) => {
        try {
            const user = await AuthUserModel.findOne({ username, password })
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
        const user = await AuthUserModel.findOne({ id: userId })
        if (!user) done(null, false)
        done(null, user)
    } catch (e: any) {
        done(e)
    }
})

passport.use(new BasicStrategy(findClient))
passport.use(new ClientPasswordStrategy(findClient))

passport.use(new BearerStrategy(async (accessToken, done) => {
    try {
        const token = await TokenModel.findOne({ token: accessToken })
        if (!token) return done(null, false)
        const user = await AuthUserModel.findOne({ id: token.userId })
        done(null, { id: user?.normalId || user?.trainerId }, <any>{ token })
    } catch (err: any) {
        done(err)
    }
}))
