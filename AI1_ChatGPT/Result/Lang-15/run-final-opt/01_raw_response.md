TARGETS: TypeUtils.getTypeArguments(Type, Class) generic superclass/interface variable resolution  
TARGETS: TypeUtils.isAssignable(Type, Type) for inherited parameterized types  
ORACLES: Existing trigger assertions: expected type-argument map size 2; Thing assignable to This<String,String>  
CASES: Resolve type variables through multi-level generic inheritance and interface parents  
CASES: Assignability where source class inherits concrete bindings for target parameterized interface/class  
CASES: Normal direct parameterized source/target; boundary raw class and Object target  
RISKS: Private parent-selection/substitution helpers may affect both APIs; avoid asserting unspecified map order  
RISKS: Context lacks nested test fixture declarations and exact generic hierarchy/bindings