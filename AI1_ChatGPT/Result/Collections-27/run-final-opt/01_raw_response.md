TARGETS: MultiValueMap.readObject(ObjectInputStream) deserialization safety validation.  
ORACLES: Trigger test expects unsafe clazz rejection during MultiValueMap deserialization.  
CASES: Deserialize a normally serialized MultiValueMap using its supported default collection factory.  
CASES: Deserialize serialized state containing an unsafe collection class; expect failure/rejection.  
RISKS: readObject is private; test through Java serialization round-trip only.  
RISKS: Available context omits the exact safe-class policy and exception type/message.