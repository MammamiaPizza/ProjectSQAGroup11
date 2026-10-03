TARGETS: _serializeAsIndex(SerializerProvider) decision method
TARGETS: serialize(Enum<?>,JsonGenerator,SerializerProvider) entry point
TARGETS: EnumSerializer(EnumValues,Boolean serializeAsIndex) constructor and its effect on
_serializeAsIndex field
ORACLES: Trigger test expects {"color":[2]} for NUMBER shape, but got {"color":["GREEN"]}
ORACLES: Default shape (no annotation) should serialize as name; explicit NUMBER shape yields index
CASES: Shape.NUMBER enum property => integer array; Shape.STRING => string array; no shape => name
default
CASES: Enum with null _serializeAsIndex (deprecated constructor) and no external config -> name
CASES: Enum with explicit _serializeAsIndex=true/false overriding any shape config -> index/name
RISKS: Unclear how SerializerProvider carries shape config (via annotation or objectMapper setting)
RISKS: Limited insight into EnumValues and EnumFormatShapeTest; need to ensure test environment
matches bug report