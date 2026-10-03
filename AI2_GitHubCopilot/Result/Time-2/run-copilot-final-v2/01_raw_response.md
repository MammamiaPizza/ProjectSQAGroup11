TARGETS: Partial.with(DateTimeFieldType,int) - duplicate-type detection in replacement logic
ORACLES: Partial constructor validates no duplicate DateTimeFieldType in types array; with() must
preserve this invariant
CASES: Replace era with year in [era,month] Partial; replace one field where new type duplicates
another; replace where types array already valid
RISKS: Only trigger info available - exact expected exception behavior from with() constructor call
unwrap not confirmed from partial-class code