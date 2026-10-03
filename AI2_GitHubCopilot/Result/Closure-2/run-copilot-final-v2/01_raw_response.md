TARGETS: visitFunction / hasUnknownOrEmptySupertype handling interface extends with missing
supertype
ORACLES: Must not throw NPE; emit diagnostic for nonexistent interface
(testBadInterfaceExtendsNonExistentInterfaces expectation)
CASES: Interface extends single nonexistent; extends mixed (1 existent,1 missing); extends valid
interface (baseline)
CASES: Interface with no extends clause (no op); multiple nonexistent extends; interface extending
self (boundary)
RISKS: NPE root cause may also affect class extends nonexistent; exact dereference unknown without
source
RISKS: Fix must handle null JSType returned for unresolved supertype gracefully across all extend
paths