TARGETS: JsonAdapterAnnotationTypeAdapterFactory.create() respecting @JsonAdapter’s nullSafe
attribute
ORACLES: Serialize null → JSON "null"; deserialize null JSON → null when nullSafe=true; no
NullPointerException
CASES: nullSafe=true with null value for both serialization and deserialization
CASES: nullSafe=false with null value – adapter may throw or passthrough (verify absence of NPE if
factory fix)
CASES: mix of nullSafe=true/false across different annotations; verify adapter wrapping logic
RISKS: Only trigger test names and NPE summary available; exact expected contract inferred from bug
#800