import express, { json, NextFunction, Request, Response } from "express"
import { eventBus } from "./middlewares/event-bus.middleware";
import TrainingController from "./controllers/training.controller";
import TrainingHandler from "./handlers/training.handler";
import fileUpload from "express-fileupload";
import morgan from "morgan";
import { registerTrainingService } from "./services/training.service";
import { registerConnection } from "./services/connection.service";
import { registerTrainingRepository } from "./services/training.repository.service";

const app = express()

app.use(morgan('dev'))
app.use(json({
    limit: 200 * 1024 * 1024
}))
app.use(fileUpload())
app.use(eventBus())

app.use(TrainingHandler.toMiddleware())
app.use(TrainingController)
app.use((error: Error, _: Request, res: Response, next: NextFunction) => {
    console.log(`Occured error: ${error.message}`)
    res.status(500).json({
        message: error.message
    })
})

app.listen(5000, async () => {
    await registerConnection()
    registerTrainingRepository()
    registerTrainingService()
    console.log('Server listening at 5000')
})
