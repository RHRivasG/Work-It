import { TrainingTaxonomy } from "../../fitness/TrainingTaxonomy";
import { AggregateRoot } from "../../shared/AggregateRoot";
import { ValidatePassword } from "../services/validate-password.service";
import { TrainerPasswordChangedEvent } from "./events/trainer-password-changed.event";
import { TrainerRegisteredEvent } from "./events/trainer-registered.event";
import { TrainerUpdatedEvent } from "./events/trainer-updated.event";

type Events =
    TrainerRegisteredEvent
    | TrainerPasswordChangedEvent
    | TrainerUpdatedEvent

export class Trainer extends AggregateRoot<Events> {
    readonly password!: string
    readonly name!: string
    readonly preferences: Set<TrainingTaxonomy> = new Set()

    static create(
        name: string,
        password: string,
        preferences: Set<TrainingTaxonomy>,
        validator: ValidatePassword
    ) {
        const trainer = new Trainer()
        validator.validate(password, "");
        trainer.apply(new TrainerRegisteredEvent(name, password, preferences))

        return trainer
    }

    changePassword(service: ValidatePassword, password: string) {
        service.validate(password, this.password)
        this.apply(new TrainerPasswordChangedEvent(this.id, password))
    }

    update(properties: Partial<Omit<Trainer, "password" | "id">>) {
        this.apply(new TrainerUpdatedEvent(properties))
    }

    protected when(event: Events): void {
        if (event instanceof TrainerUpdatedEvent)
            Object.assign(this, event.trainer);
        else if (event instanceof TrainerPasswordChangedEvent)
            Object.assign(this, { password: event.password })
        else if (event instanceof TrainerRegisteredEvent)
            Object.assign(this, { ...event })
    }

    protected invariants(): void {
        if (this.name == '')
            throw new Error("Name is not valid")
    }
}
