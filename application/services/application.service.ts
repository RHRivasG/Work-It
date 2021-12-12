export interface ApplicationService<T, R> {
    handle(command: T): Promise<R>
}
