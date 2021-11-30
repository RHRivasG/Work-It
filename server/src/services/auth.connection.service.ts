import mongoose, { Connection } from "mongoose";

export let connection: Connection

export const registerConnection = async () => {
    const mdb = await mongoose.connect("mongodb://localhost/auth");
    connection = mdb.connection
}
