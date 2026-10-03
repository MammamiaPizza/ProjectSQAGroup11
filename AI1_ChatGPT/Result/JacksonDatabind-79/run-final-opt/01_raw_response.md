TARGETS: JacksonAnnotationIntrospector object-id/reference annotation introspection and always-as-id propagation  
TARGETS: ObjectIdInfo.withAlwaysAsId(boolean) and getAlwaysAsId() state preservation  
TARGETS: BeanSerializerBase object-id writer/contextual serialization behavior  
ORACLES: Trigger expected JSON: {"alwaysClass":[1],"alwaysProp":2}  
ORACLES: @JsonIdentityReference(alwaysAsId=true) should serialize first occurrence as id, not full object  
CASES: Class-level always-as-id object in a collection serializes as [1]  
CASES: Property-level always-as-id serializes as scalar id 2  
CASES: Verify non-always-as-id identity serialization remains a full object when first encountered  
RISKS: Annotation precedence/merging may lose alwaysAsId when ObjectIdInfo is copied or replaced  
RISKS: Context is truncated; exact annotation methods and unaffected behavior are not fully available