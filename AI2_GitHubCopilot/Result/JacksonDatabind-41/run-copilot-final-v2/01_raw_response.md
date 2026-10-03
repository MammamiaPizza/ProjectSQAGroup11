TARGETS: _fromWildcard(ClassStack,WildcardType,TypeBindings);
_fromVariable(ClassStack,TypeVariable,TypeBindings)
TARGETS: _fromAny dispatch when supplying a WildcardType or TypeVariable with null/empty bounds
ORACLES: No IllegalArgumentException; wildcard with null bounds should resolve to unbounded (?) or
Object
ORACLES: TypeVariable with null bound → resolved as Object or unknownType (from fix: treat null
bounds gracefully)
ORACLES: Expected behavior: mappping null upper bound → Object.class, null lower bound → null (no
super)
CASES: Unbounded wildcard (?): upper bounds empty array or null element(s); lower bounds null/empty
CASES: Wildcard ? extends Object (normal); wildcard with explicit null in bounds array
(boundary/error)
CASES: TypeVariable with no generic bounds or bounds containing a null type (from raw class
resolution)
CASES: Nested wildcard like ? extends ? where inner bound is null; variable referencing unresolved
parameter
RISKS: Exact WildcardType/TypeVariable construction inside deprecated test unknown; only defect4j
trigger available