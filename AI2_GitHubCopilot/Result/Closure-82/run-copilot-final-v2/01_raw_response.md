TARGETS: JSType.isEquivalentTo(JSType), JSType.isSubtype(JSType)
ORACLES: testIssue301 expects a warning; testEmptyFunctionTypes expects empty function types
equivalent or subtype.
CASES: two empty FunctionTypes vs each other; empty vs non-empty; vs NoType, AllType, null/void;
nested empty.
RISKS: cannot inspect FunctionType subclass; isEquivalentTo default in JSType may not dispatch
correctly for empty functions.