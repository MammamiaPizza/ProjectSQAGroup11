TARGETS: getLine(int), getRegion(int), findLineOffsets(), getCode()
ORACLES: getLine returns exactly the substring between line-start offsets computed from code
ORACLES: getRegion returns non-null for in-range lines, null otherwise; excerpt from line 2 ->
"third line"
CASES: last line with no trailing newline, first line, middle line, empty lines, whitespace-only
lines
CASES: line numbers 0, negative, > line count, INT_MAX, after code change via setCode
RISKS: bug may involve stale cached offsets or charset decoding; unknown exact test setup
RISKS: only SourceFile internals visible; cannot inspect failing tests; may miss precondition paths
RISKS: getLine/Region may depend on getCodeReader charset; limited to Closure-56b