TARGETS: ReflectiveTypeAdapterFactory.create isPrimitive check blocks @JsonAdapter lookup;
getBoundFields annotation retrieval; BoundField.write/read serializes adapter result
ORACLES: Expected JSON {"part":["42"]} from @JsonAdapter on primitive int field; actual
{"part":[42]} (adapter unused)
CASES: @JsonAdapter on primitive field; annotation on collection element type; null field; field
override vs class annotation
RISKS: Deserialization path may ignore annotation too; interaction with Excluder; limited context on
full ReflectiveTypeAdapterFactory diff