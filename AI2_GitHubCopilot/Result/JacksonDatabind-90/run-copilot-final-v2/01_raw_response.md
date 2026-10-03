TARGETS: canInstantiate, canCreateUsingArrayDelegate, createUsingArrayDelegate,
getArrayDelegateType, getArrayDelegateCreator
ORACLES: canInstantiate==true for abstract types with array delegate; createUsingArrayDelegate
succeeds without "abstract types" error
CASES: abstract class + @JsonCreator taking array param; abstract without any creator (error);
non-abstract with array delegate; abstract with both delegate and array delegate
RISKS: No fixed source; rely on trigger test as correctness oracle; only public API; cannot verify
internal _arrayDelegateCreator field setting