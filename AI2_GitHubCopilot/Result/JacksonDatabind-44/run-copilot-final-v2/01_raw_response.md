TARGETS: SimpleType.construct(Class), _narrow(Class), constructUnsafe(Class)
ORACLES: BeanDescription.findProperties() on raw class vs. JavaType.getProperty() results
ORACLES: Expected property count from type metadata after _narrow
CASES: Subclass with extra field b not in supertype → verify resolve includes b
CASES: Default-typing scenario: use constructUnsafe with a subtype class having extra properties
CASES: construct with Map/Collection/array subclass → expect IllegalArgumentException
CASES: _narrow from a supertype to a subtype with additional fields → check property visibility
CASES: withTypeHandler then construct on a class with type-annotation; verify handler preserved
RISKS: _narrow may drop properties from the subtype, causing UnrecognizedPropertyException
RISKS: constructUnsafe may skip subtype property introspection when used with default typing