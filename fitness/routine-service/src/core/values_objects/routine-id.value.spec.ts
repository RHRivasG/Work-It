import { randomBytes } from "crypto";
import { RoutineIdEmpty } from "../errors/routine-id-empty.error";
import { RoutineId } from "./routine-id.value";

describe("Value Object RoutineId", () => {
  it("Should return a RoutineId when an id isn't passed", () => {
    expect(new RoutineId()).toBeInstanceOf(RoutineId);
  });
  it("Should return a RoutineId when an id has no -", () => {
    expect(new RoutineId("80f029e4acb04063b01c99a5027b4226")).toBeInstanceOf(
      RoutineId
    );
  });
  it("Should return a RoutineId when an id has -", () => {
    expect(
      new RoutineId("80f029e4-acb0-4063-b01c-99a5027b4226")
    ).toBeInstanceOf(RoutineId);
  });
  it("Should return a error: RoutineEmpty", () => {
    expect(() => new RoutineId("")).toThrowError(new RoutineIdEmpty());
  });
  it("Should return a error: RoutineEmpty", () => {
    expect(() => new RoutineId(" ")).toThrowError(new RoutineIdEmpty());
  });
});
