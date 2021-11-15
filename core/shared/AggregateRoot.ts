import { randomUUID } from "crypto";
import { Publisher } from "./Publisher";

export abstract class AggregateRoot<T> {
    public id: string = randomUUID()
    private publisher?: Publisher<T>

    constructor() { }

    bind(pub: Publisher<T>) {
        this.publisher = pub
        return this
    }

    protected apply<K extends T>(event: K) {
        this.when(event)
        this.invariants()
        if (this.publisher) this.publisher(event)
    }

    protected abstract when(event: T): void
    protected abstract invariants(): void
}
