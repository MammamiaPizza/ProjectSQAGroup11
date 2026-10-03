TARGETS: StdKeySerializer.serialize(Object, JsonGenerator, SerializerProvider) for map key serialization  
ORACLES: Trigger comparison: Class key String.class must produce JSON field name "java.lang.String"  
CASES: Map<Class<?>,Integer> with String.class -> {"java.lang.String":2}  
CASES: Normal non-Class key behavior should retain its existing string field-name serialization  
RISKS: Class.toString() yields "class java.lang.String"; serializer must use the correct Class name representation  
RISKS: Only trigger/spec behavior is available; no other expected key-type behavior is established