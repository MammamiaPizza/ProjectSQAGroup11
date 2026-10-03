TARGETS: POJONode.serialize(g,ctxt) must use ctxt.findValueSerializer on _value's class, not default
asText
ORACLES: testPOJONodeCustomSer expects JSON with "Str":"The value is: Hello!"; actual contains
"NULL"
CASES: String pojo + custom ser; null _value; Integer + custom ser; same serializer for multiple
types
RISKS: Cannot see serialize impl; may misunderstand how JsonGenerator writes custom result