import { TrainingTaxonomy } from "../../fitness/TrainingTaxonomy";
import { AggregateRoot } from "../../shared/AggregateRoot";
import { ValidatePassword } from "../services/validate-password.service";
import { UserPasswordChangedEvent } from "./events/user-password-changed.event";
import { UserRegisteredEvent } from "./events/user-registered.event";
import { UserUpdatedEvent } from "./events/user-updated.event";

type Events =
    UserRegisteredEvent
    | UserUpdatedEvent
    | UserPasswordChangedEvent

export class User extends AggregateRoot<Events> {
    readonly password!: string
    readonly name!: string
    readonly preferences: Set<TrainingTaxonomy> = new Set()

    static create(
        name: string,
        password: string,
        preferences: Set<TrainingTaxonomy>,
        validator: ValidatePassword
    ) {
        const user = new User()
        validator.validate(password, "")
        user.apply(new UserRegisteredEvent(name, password, preferences))
        return user
    }

    changePassword(validator: ValidatePassword, password: string) {
        validator.validate(password, this.password)
        this.apply(new UserPasswordChangedEvent(this.id, password))
    }

    update(data: Partial<Omit<User, "password" | "id">>) {
        this.apply(new UserUpdatedEvent(this.id, data))
    }

    protected when(event: Events): void {
        if (event instanceof UserRegisteredEvent)
            Object.assign(this, { ...event })
        else if (event instanceof UserPasswordChangedEvent)
            Object.assign(this, { password: event.password })
        else if (event instanceof UserUpdatedEvent)
            Object.assign(this, event.data)
    }
    protected invariants(): void {
        if (this.name == '') throw new Error("Name is not valid")
    }

}
