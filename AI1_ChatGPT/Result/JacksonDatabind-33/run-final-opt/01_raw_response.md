TARGETS: JacksonAnnotationIntrospector annotation lookup affecting @JsonUnwrapped property discovery  
TARGETS: findUnwrappingNameTransformer(AnnotatedMember) and related serialization-property indicators  
ORACLES: Existing TestUnwrapped.testUnwrappedAsPropertyIndicator expected serialization behavior  
ORACLES: Serialization must not report Outer as an empty bean for the trigger model  
CASES: @JsonUnwrapped member recognized as a serializable property indicator  
CASES: Unwrapped member with default enabled=true behavior  
CASES: Unwrapped member with prefix/suffix name transformation, if covered by available annotations  
RISKS: Exact expected JSON and trigger fixture structure are not provided in this context  
RISKS: Do not infer behavior for unrelated annotation-introspector methods