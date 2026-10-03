TARGETS: GenericMetadataSupport.extractRawTypeOf(Type) for raw generic types (e.g., List not
List<E>)
TARGETS: TypeVariableReturnType.readTypeVariables & extractRawTypeOf when typeVariable bounds is
null
TARGETS: ParameterizedReturnType.readTypeParameters handling raw actual type arguments
ORACLES: Regression: deep stub of nesting with raw type must not throw MockitoException("Raw
extraction not supported for : 'null'")
ORACLES: rawType() must return non-null Class for raw nested types (e.g., Object.class or the raw
outer type)
CASES: Deep stub when field type is Container<Raw> where Raw is a raw type of Generic<…>
CASES: Parameterized type with raw actual argument (e.g., Map<String, Raw>)
CASES: Multiple nesting levels with raw types; verify no NPE from null rawType
RISKS: Source diff not provided; only signatures available; exact bug location inferred from bug
summary "Raw extraction not supported for : 'null'"