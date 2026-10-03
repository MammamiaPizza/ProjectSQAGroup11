TARGETS: JsonMappingException.Reference.getDescription/toString; path rendering via getPathReference and getMessage  
ORACLES: Trigger comparisons require enclosing class names, including `$`, in Reference descriptions  
CASES: Reference from inner-class instance + field "inner" renders fully qualified `Outer["inner"]`  
CASES: Nested Foo["bar"] -> Bar["baz"] path retains each enclosing test class prefix  
CASES: Exercise default and @JsonCreator deserialization failures producing JsonMappingException paths  
CASES: Check field-name references and path order after prependPath/wrapWithPath  
RISKS: Exact message formatting may include base message/location; assert path reference where possible  
RISKS: No source body or baseline supplied; limit expectations to stated trigger outputs/API