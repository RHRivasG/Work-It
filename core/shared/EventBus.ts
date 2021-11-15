import { filter, Observable, Subject } from "rxjs";
export { Subscription } from "rxjs"

type Constructor<T> = { new(...args: any[]): T; }

type MappedConstructor<T extends any[]> = {
    [Index in keyof T]: Constructor<T[Index]>
}

type AnyOrAll<T extends any[]> = T extends [] ? any : T[number]

export interface EventBus {
    publish<T>(event: T): void
    forEvents<T extends any[]>(...events: MappedConstructor<T>): Observable<T[number]>
}

export class SynchronousEventBus implements EventBus {
    private innerBus = new Subject()

    publish<T>(event: T) { this.innerBus.next(event) }

    forEvents<T extends any[]>(...events: MappedConstructor<T>): Observable<AnyOrAll<T>> {
        return this.innerBus.asObservable().pipe(
            filter((evt) => events.length == 0 || events.some(event => evt instanceof event))
        )
    }
}
