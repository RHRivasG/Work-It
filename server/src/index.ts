import express, { json, Request, Response } from "express"
import { eventBus } from "./middlewares/event-bus.middleware";
import TrainingController from "./controllers/training.controller";
import RoutineController from "./controllers/routine.controller";
import TrainingHandler from "./handlers/training.handler";
import fileUpload from "express-fileupload";
import morgan from "morgan";
import { registerTrainingService } from "./services/training.service";
import { registerConnection as registerFitnessConnection } from "./services/fitness.connection.service";
import { registerConnection as registerAuthConnecetion } from "./services/auth.connection.service";
import { registerTrainingRepository } from "./services/training.repository.service";
import passport from "passport";
import login from "connect-ensure-login";
import { authorize, decision, token } from "./middlewares/oauth2.middleware";
import { registerModels, TokenModel } from "./services/entities.service";
import session from "express-session";
import errorHandler from "errorhandler";
import "./middlewares/passport.middleware";

const app = express()

app.use(morgan('dev'))
app.use(json({
    limit: 200 * 1024 * 1024
}))
app.use(fileUpload())
app.use(eventBus())
app.use(session({ secret: 'ucab work-it', resave: false, saveUninitialized: false }))
app.use(passport.initialize())
app.use(passport.session())
app.use(errorHandler())

app.use(TrainingHandler.toMiddleware())
app.use(TrainingController)
app.use(RoutineController)

app.post("/login", passport.authenticate('local'), (_, res) => {
    res.json({ msg: 'Login Successfull' })
})
app.delete("/logout", login.ensureLoggedIn(), async (req, res) => {
    const token = req.headers['Authorization']![0].replace('Bearer ', '')
    req.logout()
    await TokenModel.deleteOne({ token })
    res.json({ msg: 'Logout successfull' })
})
app.get("/authorize", login.ensureLoggedIn(), authorize(['resource']), (req: Request, res: Response) => {
    res.json({ msg: 'Decision started' })
})
app.post("/authorize/decision", login.ensureLoggedIn(), decision, (_, res) => {
    res.json({ msg: 'Decision finished' })
})
app.post("/authorize/token", passport.authenticate(['basic', 'oauth2-client-password']), login.ensureLoggedIn(), token)
app.get("/protected/resource", passport.authenticate('bearer', { session: false }), (req, res) => {
    res.json({ protected: 'resource' })
})

app.listen(5000, () => {
    registerFitnessConnection()
    registerAuthConnecetion()
    registerModels()
    registerTrainingRepository()
    registerTrainingService()
    console.log('Server listening at 5000')
})
