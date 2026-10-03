TARGETS: Java serialization/deserialization of BeanPropertyWriter, especially enum serializer handlers.  
ORACLES: TestJDKSerialization::testEnumHandlers must complete without NotSerializableException.  
CASES: Serialize/deserialize mapper or writer state involving enum property handlers; verify serialization succeeds.  
CASES: Verify post-deserialization enum handler behavior remains usable during serialization.  
RISKS: transient reflective/accessor, annotations, format, dynamic serializers, and internal settings may be lost.  
RISKS: Context lacks full BeanPropertyWriter constructor/state setup and exact expected serialized output.