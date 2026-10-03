TARGETS: RandomStringUtils.random(int,int,int,boolean,boolean,char...) range validation and exceptions.
ORACLES: Existing trigger requires invalid-range exception message to contain "start".
CASES: random(count,start,end,...) with start >= end; assert exception message includes "start".
CASES: Boundary count 0 with invalid/valid bounds; verify documented validation order only if observable.
CASES: Valid start < end outputs length count and respects requested character constraints.
CASES: char-array overload invalid/empty chars and negative count, based on exposed behavior only.
RISKS: Random output makes exact-content assertions brittle; use length/range/property assertions.
RISKS: Context lacks implementation and full tests; avoid assuming exception type or exact message beyond trigger.