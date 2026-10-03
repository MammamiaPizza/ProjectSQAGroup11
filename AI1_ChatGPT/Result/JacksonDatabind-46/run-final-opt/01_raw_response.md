TARGETS: ReferenceType.getGenericSignature(StringBuilder) for referenced generic types/arrays.  
ORACLES: Trigger TestJavaType::testGenericSignature1195 comparison failure; expected JVM signature placement.  
CASES: Reference type whose referenced type is String[]; verify generic signature ends with "[>];" not "[];>".  
CASES: Normal referenced non-array generic type; ensure signature formatting remains valid.  
CASES: Multi-dimensional/reference-array content to exercise array-marker ordering.  
ORACLES: getErasedSignature/buildCanonicalName only if existing tests establish related formatting.  
RISKS: Constructors/factories for ReferenceType and concrete JavaType setup are not provided.