TARGETS:InvocationMatcher.matches(Invocation) when actual args count != matchers count
TARGETS:InvocationMatcher.captureArgumentsFrom(Invocation) when i.getArguments() length != matchers
size
TARGETS:safelyArgumentsMatch when actualArgs.length differs from matchers.size()
ORACLES:Expected: no ArrayIndexOutOfBoundsException; matches returns false for mismatched sizes;
capture safely ignores extra matchers
CASES:Normal: matching with equal arg/matcher counts; Boundary: 0 actual args vs 1+ matchers; 1
actual vs 0 matchers
CASES:Varargs: empty vararg (0 actual) vs 1+ matchers; varargs with real args vs extra matchers
RISKS:Fix location ambiguous; could be index guard missing in captureArgumentsFrom when iterating
over matchers
RISKS:Potential similar issue in hasSimilarMethod overloadedButSameArgs path if argument array sizes
mismatch