import { EventBus, Subscription, SynchronousEventBus } from "core/shared/EventBus";

export class EventHandlerBuilder {
    private registeredHandlers: ((_: EventBus) => Subscription)[] = []
    constructor() { }

    handle(f: (_: EventBus) => Subscription) {
        this.registeredHandlers.push(f)
    }

    listen(eventBus: EventBus): Subscription[] {
        return this.registeredHandlers.map(handler => {
            return handler(eventBus)
        })
    }
}

export let eventBus: EventBus

export const registerEventBus = () => {
    eventBus = new SynchronousEventBus()
}
