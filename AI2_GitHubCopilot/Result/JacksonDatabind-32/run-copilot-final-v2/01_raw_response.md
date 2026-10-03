TARGETS: UntypedObjectDeserializer.deserialize, mapObject, mapArray; handle END_OBJECT inside nested
ORACLES: nested untyped JSON (e.g., {"a":{"b":1}}) deserializes to Map with nested Map without
exception
CASES: map containing map, map containing list, deeply nested, empty nested object, array of maps
CASES: boundary: JSON with unmatched braces, null values, keys that are strings, numbers as values
RISKS: need DeserializationContext mock; no direct access to inner Vanilla class behavior;
mapObject/mapArray protected