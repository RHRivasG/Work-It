import mongoose, { Connection } from "mongoose";

export let connection: Connection

export const registerConnection = async () => {
    await mongoose.connect('mongodb://localhost/fitness')
    connection = mongoose.connection
}
