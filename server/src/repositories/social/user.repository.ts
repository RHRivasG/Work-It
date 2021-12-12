import { UserRepository } from "application/repositories/social/user.repository.interface";
import { User } from "core/social/users/user";
import { UserDO } from "../../entities/social/user.entity";
import { UserModel } from "../../services/entities.service";

declare module "core/social/users/user" {
    namespace User {
        export let fromDO: (userDO: UserDO) => User
    }
    interface User {
        toDO(): UserDO
    }
}

User.fromDO = (userDO: UserDO) => {
    const userModel = new User()
    Object.assign(
        userModel,
        {
            id: userDO.id,
            name: userDO.username,
            password: userDO.password,
            trainingTaxonomy: new Set(userDO.trainingTaxonomy)
        }
    )
    return userModel
}

User.prototype.toDO = function() {
    return {
        id: this.id,
        username: this.name,
        password: this.password,
        trainingTaxonomy: Array.from(this.preferences)
    }
}

export class MongoUserRepository implements UserRepository {
    async get(id: string) {
        const userDo = await UserModel.findOne({ id })
        if (userDo) return User.fromDO(userDo)
    }
    async delete(user: User) {
        await UserModel.deleteOne({ id: user.id })
    }
    async save(user: User) {
        const userDO = user.toDO()
        await UserModel.findOneAndUpdate({ id: userDO.id }, { $set: userDO }, { upsert: true })
    }
    async getAll() {
        const documents = await UserModel.find({})
        return documents.map(User.fromDO)
    }

}
