TARGETS: MathArrays.linearCombination(double[],double[]) dot product, especially length==1 arrays.
TARGETS: Avoid indexing second element (e.g. b[1]/prodHigh[1]) when arrays have only one item.
ORACLES: MathArraysTest.testLinearCombinationWithSingleElementArray expects scalar product a[0]b[0].
ORACLES: Mathematical dot product sum(a[i]b[i]) for equal-length double arrays.
CASES: len1 normal: {2}{3}=6, {0}{7}=0, {-4}{5}=-20.
CASES: len1 fractional: {0.5}{0.25}=0.125 and {-0.5}*{-0.25}=0.125.
CASES: len2/len3/len4 multi-element arrays exercising the compensated-sum branch.
CASES: len0 arrays only if covered by tests; expected result not shown.
RISKS: Length-mismatch and NaN/Infinity semantics/exception types absent from supplied summary.
RISKS: For low-bit compensation, assert using small integer-coefficient products.