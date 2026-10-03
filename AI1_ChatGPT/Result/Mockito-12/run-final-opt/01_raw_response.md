TARGETS: GenericMaster.getGenericType(Field): resolve a field's generic type without casting ParameterizedType to Class.  
ORACLES: Existing GenericMasterTest nested-generics and captor annotation trigger outcomes are the expected-result source.  
CASES: Plain Class-typed field returns its declared class.  
CASES: Parameterized field such as List<String> must not throw ClassCastException.  
CASES: Nested parameterized generic fields exercise the reported failing path.  
CASES: Captor fields with generic, raw, inherited, and ordinary annotations must initialize without this exception.  
RISKS: Only getGenericType signature/body is provided; exact expected class for parameterized/nested types is unspecified.