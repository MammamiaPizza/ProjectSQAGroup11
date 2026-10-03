TARGETS: visitGetProp, propertyIsImplicitCast, getJSType, ensureTyped, visitObjLitKey
ORACLES: Warning message "element {0} does not exist on this enum"
ORACLES: Warning "property {0} never defined on {1}" for missing union-member property
CASES: Property access on union including enum type; each union member lacks the property
CASES: Property access on union including interface; union with null/void
CASES: Boundary: property exists on all union members, expecting no warning
CASES: Error case: getprop on union where one member is an enum without that element
RISKS: Bug may stop checking at first union member that has the property, missing later undefined
RISKS: Limited context from bug version; cannot see other union-checking paths beyond getprop