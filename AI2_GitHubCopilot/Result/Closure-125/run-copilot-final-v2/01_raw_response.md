TARGETS: TypeCheck.process, visitFunction (interface body checks), visitAssign, checkPropCreation,
ensureTyped
ORACLES: No IllegalStateException; correct interface/enum error messages per spec; valid inputs pass
silently
CASES: Interface function with non-empty body --> error, empty body --> ok; enum key redefinition;
enum key not syntactic constant; property redefinition on interface; typeof string check; return
type mismatch on interface function
RISKS: Exact IllegalStateException trigger path unknown from signatures alone; coverage of
enum/interface corner cases limited