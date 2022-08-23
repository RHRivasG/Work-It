export class RoutineTrainings {
  public readonly value: Uint8Array[];
  constructor(value: string[]) {
    this.value = value.map(
      (training) => new Uint8Array(Buffer.from(training, "hex"))
    );
  }
}
