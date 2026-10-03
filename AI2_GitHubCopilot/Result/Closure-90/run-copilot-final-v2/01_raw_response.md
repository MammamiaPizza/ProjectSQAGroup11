TARGETS: FunctionTypeBuilder.isFunctionTypeDeclaration(JSDocInfo) must recognise @typedef
TARGETS: FunctionType.isInterface, isConstructor, getInstanceType; maybeSetBaseType;
setPrototypeBasedOn
ORACLES: testBackwardsTypedefUse8 expects no unexpected type-check warnings
ORACLES: testBackwardsTypedefUse9 expects no "actual parameter … does not match formal parameter"
CASES: backward typedef of function type (typedef after function expression); forward typedef; no
typedef
CASES: @typedef applied to an existing constructor/interface (invalid); nested typedef of function
type
CASES: variadic parameters, empty params, single param; typedef used in a call with wrong argument
count
RISKS: only trigger names available; must infer expected outcome from bug summary and test names
RISKS: change to isFunctionTypeDeclaration may affect non-typedef function declarations or @extends
validation