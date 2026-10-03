TARGETS: TypeDeserializerBase._handleUnknownTypeId and its interaction with deserialization problem handling.  
TARGETS: _findDefaultImplDeserializer; native-type-id path only if reachable from unknown-id handling.  
ORACLES: Trigger test: handler-managed unknown type id must not end in wrapped NullPointerException.  
ORACLES: JsonMappingException chain identifies GenericContent.innerObjects ArrayList element index 1.  
CASES: Unknown type id for a polymorphic value inside a collection, with a DeserializationProblemHandler installed.  
CASES: Unknown-id handler resolution versus absent/default implementation behavior; verify no null dereference.  
CASES: Boundary coverage for missing type id via _handleMissingTypeId if directly testable.  
RISKS: Context omits handler return value, JSON input, and exact expected deserialized object.