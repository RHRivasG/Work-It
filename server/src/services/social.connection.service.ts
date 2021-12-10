import mongoose, { Connection } from "mongoose";

export let connection: Connection

export const registerConnection = () => {
    connection = mongoose.createConnection("mongodb://localhost/social")
}
