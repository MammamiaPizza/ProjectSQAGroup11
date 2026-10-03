TARGETS: BeanDeserializerFactory.isPotentialBeanType() and property-type validation during bean
setup
TARGETS: Checks for illegal types: Throwable, Proxy, Enum, Map, Collection, etc.
ORACLES: Exception thrown must contain substring "Illegal type" (per testIssue1599 assertion)
ORACLES: Legitimate bean types (concrete, non-proxy) are allowed without false positives
CASES: Bean property of Throwable subclass; property of Proxy subclass; property of Map (if blocked)
CASES: Target type is illegal (deserialize directly as Throwable); nested generic with illegal
parameter
CASES: Constructor parameter, setter, or field of illegal type; default value with illegal type
CASES: Boundary: abstract class property vs concrete safe type; custom deserializer override
rejection
RISKS: Exact modified method unknown; factory config (USE_GETTERS_AS_SETTERS) may alter behavior
RISKS: Limited snippet; message format may differ; test may need to cover all unsafe type categories