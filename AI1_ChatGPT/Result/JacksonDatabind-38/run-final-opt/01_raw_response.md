TARGETS: Deprecated CollectionType.construct, MapType.construct, and SimpleType.construct type construction.
ORACLES: Trigger assertions require declared Point content/value type, not LinkedHashMap.
ORACLES: Trigger POJO subtype deserialization must accept inherited Point field x with Point3D z.
CASES: Explicit MapType with Point value; deserialize object value and assert Point runtime class.
CASES: Explicit CollectionType with Point element; deserialize object element and assert Point runtime class.
CASES: POJO subtype input containing inherited x and subtype z.
CASES: Deprecated SimpleType.construct for ordinary POJO; Map, Collection, and array rejection.
RISKS: Construction metadata/bindings may lose generic content type and cause untyped LinkedHashMap fallback.
RISKS: Available context omits full method bodies and trigger JSON/mapper setup.