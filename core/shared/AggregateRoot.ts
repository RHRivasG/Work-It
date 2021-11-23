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
        this.invariants()
        this.when(event)
        if (this.publisher) this.publisher(event)
    }

    protected abstract when(event: T): void
    protected abstract invariants(): void
}
