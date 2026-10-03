TARGETS: InvocationMatcher.matches(), hasSimilarMethod(), safelyArgumentsMatch(), hasSameMethod()
ORACLES: matches() return true when mock, method name, and argument matchers match, even for
inherited generics
CASES: exact same method; overriding generic method (erased args match); overloaded generic (diff
param types); null args
CASES: varargs; matchers mix raw/any; multiple invocations; unverified vs verified candidate
RISKS: exact bug location unknown; expected behavior inferred from triggers; no access to fixed code
for comparison