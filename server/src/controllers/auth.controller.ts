import { Router } from "express";
import passport from "passport";
import login from "connect-ensure-login";
import { authorize, token } from "../middlewares/oauth2.middleware";
import { retriveCredentialsInBody, storeCredentials, storeToken } from "../middlewares/auth.middleware";
import "../middlewares/passport.middleware";

const AuthController = Router()

AuthController.route("/login")
    .get(
        (_, res) => {
            res.status(403)
            res.send('Not Logged In')
        }
    )
    .post(
        passport.authenticate('local'),
        (_, res) => res.json({ msg: 'Login Successfull' })
    )
    .delete(
        passport.authenticate('bearer', { session: false }),
        async (req, res) => {
            const authToken = req.authInfo!.token
            req.logout()
            await authToken.delete()
            res.json({ msg: 'Logout successfull' })
        }
    )

AuthController.get(
    "/authorize/normal",
    login.ensureLoggedIn(),
    storeCredentials("routines", "profile"),
    authorize,
    (_, res) => res.send('TODO')
)

AuthController.get(
    "/authorize/trainer",
    login.ensureLoggedIn(),
    storeCredentials("routines", "trainings", "profile"),
    authorize,
    (_, res) => res.send('TODO')
)

AuthController.get(
    "/authorize/token",
    login.ensureLoggedIn(),
    retriveCredentialsInBody,
    passport.authenticate('oauth2-client-password'),
    storeToken,
    token
)

export default AuthController
