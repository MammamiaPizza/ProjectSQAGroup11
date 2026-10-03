TARGETS: com.google.javascript.jscomp.CodeConsumer — formatting when right operand of subtraction is
negative-zero literal.
ORACLES: Expected string "x- -0.0" (space after minus) from testMinusNegativeZero; absence of space
indicates bug.
ORACLES: isNegativeZero(double) must return true for -0.0; used to decide output spacing/elision.
CASES: Normal: -0.0 literal, +0.0 literal, -1.0, category: subtraction of negative zero vs other
negatives.
CASES: Boundary: -0.0 from variable, Double.NEGATIVE_INFINITY, NaN, -0.0 returned by function call.
CASES: Edge: subtraction chain involving -0.0, unary minus before -0.0, division/multiplication
producing -0.0.
RISKS: Only one known test; CodeConsumer is abstract; actual formatting logic may reside in subclass
CodePrinter.
RISKS: isNegativeZero static but no access to where it is called; limited visibility into output
rules.