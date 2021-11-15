import { randomUUID } from "crypto";
import { Publisher } from "./Publisher";

export abstract class Entity<T> {
    id: string
    private publisher?: Publisher<T>

    bind(pub: Publisher<T>) {
        this.publisher = pub
        return this
    }

    constructor() {
        this.id = randomUUID()
    }

    apply(event: T) {
        this.when(event)
    }

    protected abstract when(event: T): void
}
