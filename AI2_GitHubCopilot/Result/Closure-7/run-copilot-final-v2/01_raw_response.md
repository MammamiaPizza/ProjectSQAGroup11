TARGETS: caseUnionType, getNativeTypeForTypeOf, getRestrictedWithoutNull/Undefined,
RestrictByTypeOfResultVisitor subclasses
ORACLES: testTypeof3/testGoogIsFunction2 expectations; typeof-restricted union must match expected
JSType union
CASES: Union with Function+null+void+string+boolean+number; typeof==='function' narrows to Function
only (fix ensures proper restriction)
CASES: Union with all top types; typeof==='object' should exclude null/void but keep Object+array
etc.
CASES: Union reduced by typeof to multiple subtypes; verify returned union exactly matches expected
set
CASES: Single-element union; typeof matches → type unchanged; typeof mismatch → narrowed to NoType
CASES: Empty union/NoType/UnknownType boundaries with typeof restrictions
RISKS: Abstract class requires concrete subclass (e.g., ClosureReverseAbstractInterpreter); assert
depends on precise JSType equality