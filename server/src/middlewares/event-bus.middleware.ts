import { EventBus, Subscription, SynchronousEventBus } from "core/shared/EventBus"
import { Application, RequestHandler } from "express"

declare global {
    namespace Express {
        interface Request {
            emitEvent<T>(evt: T): void
        }
        interface Application {
            getEventBus(): EventBus
        }
    }
}

function handleEventWith(f: (_: EventBus) => Subscription): RequestHandler {
    let subscription: Subscription | undefined = undefined;
    return ({ app }, _, next) => {
        if (app.getEventBus() && !subscription) {
            subscription = f(app.getEventBus())
            app.on('close', () => subscription!.unsubscribe())
            app.on('error', () => subscription!.unsubscribe())
        }
        next()
    }
}

export function registerEventBus(app: Application) {
    const eventBus = new SynchronousEventBus()
    app.getEventBus ||= () => eventBus
    return app.getEventBus() || eventBus
}

export function eventBus(): RequestHandler {
    return (req, _, next) => {
        const eventBus = registerEventBus(req.app)
        req.emitEvent ||= <T>(evt: T) => {
            eventBus.publish(evt)
        }
        next()
    }
}

type SubscriptionContext = (app: Application, fn: (...args: any[]) => void) => void
export class EventHandlerBuilder {

    private registeredHandlers: ((_: EventBus) => Subscription)[] = []
    constructor() {}

    handle(f: (_: EventBus) => Subscription) {
        this.registeredHandlers.push(f)
    }

    toMiddleware(): RequestHandler {
        return this.registeredHandlers
            .map(handleEventWith)
            .reduce(
                (prev, current) => (req, res, next) => current(req, res, () => prev(req, res, next))
                , (_0, _1, next) => next()
            )
    }

    protected listen(app: Application, ctx: SubscriptionContext): void  {
        this.registeredHandlers.forEach(handler => ctx(app, () => {
            const sub = handler(app.getEventBus())
            app.on('close', () => sub.unsubscribe())
            app.on('error', () => sub.unsubscribe())
        }))
    }

    static listen(app: Application, ctx: SubscriptionContext, builders: EventHandlerBuilder[]) {
        builders.forEach(builder => builder.listen(app, ctx))
    }
}
