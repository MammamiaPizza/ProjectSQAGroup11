TARGETS: GenericMaster.getGenericType(Field) should resolve raw Class from fields with generic
types.
ORACLES: For Field with List<String> → getGenericType must return List.class; no ClassCastException.
ORACLES: For Field with simple type String → getGenericType must return String.class.
CASES: Parameterized type: List<String> (1 level), nested generics: List<Set<String>>.
CASES: Non-generic field: normal, null type (should not happen), or raw type field.
CASES: Edge: field with wildcard bounds (? extends Number) – raw type expected from upper bound
class.
RISKS: The snippet returns Object.class; the actual buggy version (missing) might do unsafe (Class)
cast on getGenericType().
RISKS: Potential for TypeVariable or GenericArrayType in field, but captor usage likely avoids them.
RISKS: No access to original failing code; behavior inferred from ClassCastException and test names.