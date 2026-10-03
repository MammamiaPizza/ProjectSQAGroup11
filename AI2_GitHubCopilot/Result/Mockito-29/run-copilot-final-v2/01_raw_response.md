TARGETS: Same(Object) constructor, Same.matches(Object), and Same.describeTo(Description) /
appendQuoting.
ORACLES: matches must be identity comparison (==), returning false instead of throwing on null
wanted.
ORACLES: null wanted matches only a null actual; a non-null actual must return false without NPE.
ORACLES: describeTo must not call wanted.toString() unguarded; null wanted renders as "null".
CASES: wanted=null with actual=null (true) and actual=non-null (false).
CASES: identical references (true), equal-but-distinct objects (false), and distinct non-null
values.
CASES: describeTo with null wanted and with String/Character wanted (quoting paths).
RISKS: Exact Description text/format is not specified; no source beyond supplied signatures.
RISKS: Behavior must mirror standard ArgumentMatcher/Hamcrest identity semantics; avoid asserting
undocumented message text.