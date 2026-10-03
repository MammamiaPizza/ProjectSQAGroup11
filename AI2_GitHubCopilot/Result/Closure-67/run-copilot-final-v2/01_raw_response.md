TARGETS: process(), ProcessProperties.visit(), processPrototypeParent(), addSymbolUse(), NameInfo
ORACLES: expected removal decisions from RemoveUnusedPrototypeProperties (aliasing7 should not flag
used prop)
CASES: aliasing via var, chained aliases, alias used in nested function, alias in conditional,
assignment after alias
RISKS: no test harness provided; bug details (report 459) not available; limited compiler context
for mocking inputs