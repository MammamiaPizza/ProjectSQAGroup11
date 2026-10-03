TARGETS: TypeInfoFactory.getTypeInfoForField(Field, Type) type-variable resolution for non-parameterized defining types  
TARGETS: TypeInfoFactory.getTypeInfoForArray(Type) array type information creation  
ORACLES: Existing TypeVariableTest.testSingle is the expected-result source  
ORACLES: Buggy behavior throws UnsupportedOperationException for a class where a parameterized type is expected  
CASES: Field declared with a type variable and defining type TypeVariableTest.Bar (a Class, not ParameterizedType)  
CASES: Normal parameterized defining type and matching type-variable index resolution  
CASES: Array inputs whose component type is a class or generic/type-variable type  
RISKS: TypeInfoFactory is package-private; tests may need coverage through existing Gson-facing behavior  
RISKS: Context provides no TypeInfo/TypeInfoArray contracts or expected resolved type details