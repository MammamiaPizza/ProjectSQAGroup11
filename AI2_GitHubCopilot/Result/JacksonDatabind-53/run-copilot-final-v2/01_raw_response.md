TARGETS: TypeBindings.create(Class,JavaType[]) for Map; withUnboundVariable for map; getBoundType
for "K"/"V".
ORACLES: getBoundName/getBoundType return correct names/types; withUnboundVariable preserves
existing bindings; TypeFactory constructs MapType with abstract value type.
CASES: Normal: Map<String,HasUniqueId>; boundary: single param type, empty bindings, mismatch param
count -> IllegalArgumentException.
RISKS: No concrete test data; need to infer fix from TypeBindings internal handling of type
variables; integration-level test may require JSON deserialization.