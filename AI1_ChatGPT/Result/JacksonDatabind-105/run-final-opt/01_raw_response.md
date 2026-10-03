TARGETS: JdkDeserializers.find(Class<?>, String), especially lookup for java.lang.Void  
ORACLES: Trigger expects Void deserialization not to throw MismatchedInputException for numeric JSON 123  
CASES: Deserialize numeric scalar 123 as Void; verify successful Void/null result  
CASES: Verify existing UUID, StackTraceElement, AtomicBoolean, and ByteBuffer lookup paths remain available  
RISKS: Void has no value instances; expected successful result representation is not fully specified here  
RISKS: Context provides only partial JdkDeserializers source and no direct find() behavior contract