TARGETS: TypeInference.traverse, ensurePropertyDefined, updateTypeOfParameters, traverseCall,
traverseAssign
ORACLES: Verify TypeCheckTest produces no unexpected warnings; assert specific warnings absent
ORACLES: Compare inferred JSType for property access nodes against expected types (ObjectType,
UnionType, etc.)
CASES: Assign object literal with typed prop to var, pass var.prop to function expecting that type
=> no spurious warning
CASES: Nested property access after object literal assignment => should not produce "Property X
never defined" warning
CASES: Property accessed on union type where only one side defines property => check optional
warning
CASES: Object literal with function-valued property used as callback => correctly infer parameter
type
CASES: Traverse return type of function containing property-based dispatch
RISKS: Missing knowledge of exact fix; tests may pass on buggy version if bug only surfaces under
complex compilation
RISKS: Cannot test full warning suppression without access to diagnostic groups used by
JSTypeValidator