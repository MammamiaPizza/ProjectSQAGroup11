TARGETS: FunctionTypeBuilder.maybeSetBaseType, getOrCreateConstructor, ExtendedTypeValidator,
ImplementedTypeValidator
ORACLES: Expect warning "@extends/@implements used without @constructor or @interface"; triggered
tests check warning presence/absence
CASES: Normal: @constructor+@extends; Boundary: @extends alone on non-constructor; Error:
@implements without @interface on function; mismatched param types
RISKS: UnknownFunctionContents placeholder obscures actual contents; oldType nullable param may mask
base-type resolution paths