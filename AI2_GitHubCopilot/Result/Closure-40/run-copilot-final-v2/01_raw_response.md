TARGETS: NameAnalyzer.process, FindDeclarationsAndSetters.recordSet, referenceAliases,
replaceWithRhs
ORACLES: No RuntimeException/AssertionError from process; correct variable removal in compiler
output
CASES: Input with prototype chains & class-defining functions; hidden alias scenarios;
depth/boundary nesting
RISKS: Exact issue284 trigger JS unknown; bug may involve complex alias→prototype membership
resolution failures