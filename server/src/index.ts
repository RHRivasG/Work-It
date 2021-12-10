import express, { json } from "express"
import TrainingController from "./controllers/training.controller";
import RoutineController from "./controllers/routine.controller";
import AuthController from "./controllers/auth.controller";
import TrainingHandler from "./handlers/training.handler";
import fileUpload from "express-fileupload";
import passport from "passport";
import session from "express-session";
import errorHandler from "errorhandler";
import morgan from "morgan";
import { registerTrainingService } from "./services/training.service";
import { registerConnection as registerFitnessConnection } from "./services/fitness.connection.service";
import { registerConnection as registerAuthConnecetion } from "./services/auth.connection.service";
import { registerConnection as registerSocialConnection } from "./services/social.connection.service";
import { registerTrainingRepository } from "./services/training.repository.service";
import { registerModels } from "./services/entities.service";
import { eventBus, registerEventBus } from "./services/event-bus.service";
import { registerUserRepository } from "./services/user.repository.service";
import { registerUserService } from "./services/user.service";
import { registerRoutineService } from "./services/routine.service";
import { registerRoutineRepository } from "./services/routine.repository.service";

const app = express()

app.use(morgan('dev'))
app.use(json({
    limit: 200 * 1024 * 1024
}))
app.use(fileUpload())
app.use(session({ secret: 'ucab work-it', resave: false, saveUninitialized: false }))
app.use(passport.initialize())
app.use(passport.session())
app.use(errorHandler())

app.use(TrainingController)
app.use(RoutineController)
app.use(AuthController)

app.listen(5000, () => {
    // Register services
    registerEventBus()
    registerFitnessConnection()
    registerAuthConnecetion()
    registerSocialConnection()
    registerModels()
    registerTrainingRepository()
    registerTrainingService()
    registerUserRepository()
    registerUserService()
    registerRoutineRepository()
    registerRoutineService()

    // Register command handlers
    const subscriptions = [
        ...TrainingHandler.listen(eventBus)
    ]

    app.on('exit', () => subscriptions.forEach(s => s.unsubscribe()))

    // Log server initialization
    console.log('Server listening at 5000')
})
