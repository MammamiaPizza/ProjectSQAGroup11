TARGETS: traverseAssign, ensurePropertyDefined — missing warning on property assignment to
undeclared/unknown type
ORACLES: testIssue1056 expects a JSError/warning (assertion: "expected a warning"); JSType checks on
rightType
CASES: assigning property to union/unknown object; assigning to null/undefined receiver; nested
property writes; getprop with undeclared property
RISKS: exact warning text unknown; may depend on externs/type registry state; implicit property
creation on Object vs typed objects varies