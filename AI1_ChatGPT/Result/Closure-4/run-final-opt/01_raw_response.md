TARGETS: NamedType resolution/cycle handling via getReferencedType and handleTypeCycle.
TARGETS: NamedType validator behavior via setValidator during recursive type resolution.
ORACLES: Trigger TypeCheckTest expected diagnostics and warning counts are the result source.
CASES: Implements loop should report inheritance-cycle parse error, not "can only implement interfaces".
CASES: Extends/implements loop should avoid the extra JSC_IMPLEMENTS_NON_INTERFACE warning.
CASES: Interface-to-recursive-constructor conversion must terminate without StackOverflowError.
RISKS: Private cycle helpers require exercising NamedType through compiler/type-check integration.
RISKS: Available context omits constructors/resolution entry points and exact diagnostic assertion APIs.