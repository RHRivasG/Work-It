import mongoose, { Connection } from "mongoose";

export let connection: Connection

export const registerConnection = async () => {
    connection = mongoose.createConnection('mongodb://localhost/fitness')
}
