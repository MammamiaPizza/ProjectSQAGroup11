TARGETS: TypeAdapters.JSON_ELEMENT read/write and its JsonElement type-hierarchy factory behavior.
ORACLES: Trigger expects type mismatch not to ClassCastException when JsonPrimitive is requested as JsonObject.
CASES: Deserialize primitive JSON into JsonObject; assert the documented mismatch exception/result from existing tests.
CASES: Deserialize object/array/null through JsonElement adapter; preserve corresponding JsonElement subtype/value.
RISKS: Context omits full JSON_ELEMENT implementation and exact expected exception type/message.
RISKS: Do not infer behavior from another version; use Gson-2b tests/API only.