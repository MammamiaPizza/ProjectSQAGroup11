TARGETS: TypeParser.parse(String canonical) -> JavaType; parseType internal parsing
TARGETS: TypeFactory._fromWildcard(WildcardType, TypeBindings) and _fromVariable resolution
ORACLES: Expected JavaType from TypeFactory.constructType(rawClass) or known TypeFactory constants
(CORE_TYPE_STRING)
CASES: Canonical primitive names: "int", "boolean", "long"
CASES: Simple class names: "java.lang.String", "java.util.Date"
CASES: Parameterized generics: "java.util.List<java.lang.String>"
CASES: Wildcards: "java.util.List<?>", "? extends Number", "? super Integer"
CASES: Arrays: "java.lang.String[]", "int[][]"
RISKS: NullPointerException from null TypeBindings when parsing wildcard/var canonical names
RISKS: findClass returning null or _typeCache miss; context limit: no actual test code