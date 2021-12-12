import { Document, Model } from "mongoose";
import { AuthorizationCodeDO, AuthorizationCodeEntity } from "../entities/auth/authorizationCode.entity";
import { ClientDO, ClientEntity } from "../entities/auth/client.entity";
import { TokenDO, TokenEntity } from "../entities/auth/token.entity";
import { UserDO as AuthUserDO, UserEntity as AuthUserEntity } from "../entities/auth/user.entity";
import { UserDO, UserEntity } from "../entities/social/user.entity";
import { TrainingDO, TrainingEntity } from "../entities/fitness/training.entity";
import { TrainingVideoDO, TrainingVideoEntity } from "../entities/fitness/trainingVideo.entity";
import { connection as fitnessConnection } from "./fitness.connection.service";
import { connection as socialConnection } from "./social.connection.service";
import { connection as authConnection } from "./auth.connection.service";
import { RoutineDO, RoutineEntity } from "../entities/fitness/routine.entity";

export let TrainingModel: Model<TrainingDO & Document>
export let TrainingVideoModel: Model<TrainingVideoDO & Document>

export let AuthorizationCodeModel: Model<AuthorizationCodeDO & Document>
export let ClientModel: Model<ClientDO & Document>
export let TokenModel: Model<TokenDO & Document>
export let AuthUserModel: Model<AuthUserDO & Document>
export let RoutineModel: Model<RoutineDO & Document>
export let UserModel: Model<UserDO & Document>

export const registerModels = () => {
    UserModel = socialConnection.model<UserDO & Document>("User", UserEntity)

    TrainingVideoModel = fitnessConnection.model<TrainingVideoDO & Document>("TrainingVideo", TrainingVideoEntity)
    TrainingModel = fitnessConnection.model<TrainingDO & Document>("Training", TrainingEntity)
    RoutineModel = fitnessConnection.model<RoutineDO & Document>("Routine", RoutineEntity)

    AuthorizationCodeModel = authConnection.model<AuthorizationCodeDO & Document>("AuthorizationCode", AuthorizationCodeEntity)
    ClientModel = authConnection.model<ClientDO & Document>("Client", ClientEntity)
    UserModel = authConnection.model<UserDO & Document>("User", AuthUserEntity)
    TokenModel = authConnection.model<TokenDO & Document>("Token", TokenEntity)
}
