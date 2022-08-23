export class RoutineTrainingId {
  public readonly value: Uint8Array;
  constructor(value: string) {
    this.value = new Uint8Array(
      Buffer.from(value.replaceAll("-", "").trim(), "hex")
    );
  }
}
