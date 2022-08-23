import { randomBytes, randomFill } from "crypto";
import { RoutineIdEmpty } from "../errors/routine-id-empty.error";

export class RoutineId {
  public readonly value: Uint8Array;
  constructor(value?: string) {
    if (value == null) {
      this.value = new Uint8Array(randomBytes(16));
      return;
    }

    value = value.replaceAll("-", "").trim();
    if (value.length == 0) throw new RoutineIdEmpty();

    this.value = new Uint8Array(Buffer.from(value, "hex"));
  }
}
