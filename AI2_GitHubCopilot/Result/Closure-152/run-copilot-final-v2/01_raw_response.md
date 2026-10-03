TARGETS: setPrototypeBasedOn(ObjectType), setImplementedInterfaces(List),
getImplementedInterfaces(), getInstanceType(), getTypeOfThis(), getSuperClassConstructor()
TARGETS: getTopMostDefiningType(String), setPrototype(FunctionPrototypeType) (may propagate casts)
ORACLES: JSType hierarchy: ObjectType is base for objects; StringType, NumberType, VoidType are not
ObjectType; cast must be guarded.
ORACLES: UnionType can contain mixed types (Object+non-Object); backward typedef may produce such
unions; code must check instanceof ObjectType.
CASES: Normal: setPrototypeBasedOn with a concrete ObjectType (e.g., NamedType); no exception;
instance type returned correctly.
CASES: Boundary: setPrototypeBasedOn with StringType argument; should not throw ClassCastException;
expect safe fallback (null/error type).
CASES: Boundary: setImplementedInterfaces with list containing a UnionType element; avoid
ClassCastException; iterate only ObjectType items.
CASES: Error: setImplementedInterfaces with a null element or all non-ObjectType entries; validate
graceful handling or NPE.
CASES: Scenario: typedef creates union instance; call getTypeOfThis() → return type is union; verify
no cast error.
RISKS: Exact cast location unknown without source diff; reproducing ClassCastException requires full
registry setup and backward typedef resolution; fix behavior may vary.