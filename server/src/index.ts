import express, { json, NextFunction, Request, Response } from "express"
import { eventBus } from "./middlewares/event-bus.middleware";
import TrainingController from "./controllers/training.controller";
import RoutineController from "./controllers/routine.controller";
import TrainingHandler from "./handlers/training.handler";
import fileUpload from "express-fileupload";
import morgan from "morgan";
import { registerTrainingService } from "./services/training.service";
import { registerConnection as registerFitnessConnection } from "./services/fitness.connection.service";
import { registerTrainingRepository } from "./services/training.repository.service";
import passport from "passport";
import { ensureLoggedIn } from "connect-ensure-login";
import { authorize, decision, token } from "./middlewares/oauth2.middleware";

const app = express()

app.use(morgan('dev'))
app.use(json({
    limit: 200 * 1024 * 1024
}))
app.use(fileUpload())
app.use(eventBus())
app.use(passport.initialize())
app.use(passport.session())

app.use(TrainingHandler.toMiddleware())
app.use(TrainingController)
app.use(RoutineController)
app.use((error: Error, _: Request, res: Response) => {
    console.log(`Occured error: ${error.message}`)
    res.status(500).json({
        message: error.message
    })
})

app.post("/login", passport.authenticate('local'))
app.delete("/logout", (req, res) => {
    req.logout()
    res.json({ msg: 'Logout successfull' })
})
app.get("/authorize", ensureLoggedIn(), authorize)
app.get("/authorize/decision", ensureLoggedIn(), decision)
app.get("/authorize/decision/token", passport.authenticate(['basic', 'oauth2-client-password']), ensureLoggedIn(), token)
app.get("/protected/resource", passport.authenticate('bearer', { session: false }), (req, res) => {
    res.json({ protected: 'resource' })
})

app.listen(5000, async () => {
    await registerFitnessConnection()
    registerTrainingRepository()
    registerTrainingService()
    console.log('Server listening at 5000')
})
