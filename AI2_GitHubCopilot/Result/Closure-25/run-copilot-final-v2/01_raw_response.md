TARGETS traverseNew, backwardsInferenceFromCallSite, traverseAssign, ensurePropertyDefined
ORACLES testBackwardsInferenceNew expected: property 'foo' inferred as (number|undefined) from new
constructor return type
CASES new on constructor returning primitive (number|undefined), new on void function, new on
function returning object, nested property assign
CASES boundary: assign new to existing property, reassign later with different type, new inside
conditional, new in loop
RISKS internal type toString format may change; backwards inference changes could break other call
patterns like regular function calls