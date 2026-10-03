TARGETS: MapDeserializer.deserialize and resolve with configured custom map value deserializer
ORACLES: Trigger test testCustomMapValueDeser735 asserts expected map value 1, not 100
CASES: Deserialize an object/map whose values use the trigger's custom value deserializer
CASES: Verify each relevant entry retains the custom-deserialized value in the resulting Map
CASES: Cover default map creation path used by deserialize
RISKS: Context omits trigger JSON, map type, custom deserializer implementation, and intended broader semantics