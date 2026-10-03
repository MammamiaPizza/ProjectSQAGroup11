TARGETS: tryFoldComparison, compareAsNumbers, compareToUndefined (binary comparison constant
folding).
ORACLES: Expected results from ECMAScript spec for relational/equality operators with NaN,
undefined, +0/-0.
CASES: Known numeric constants (e.g., 5 < 3 → false); NaN vs any value → false; undefined
comparisons per spec.
CASES: Commutative folding: a > b must fold identically to b < a for all constant operand pairs.
CASES: Invertible operators: !(a >= b) must fold to a < b when operands are constants.
CASES: Edge values: +0 vs -0 (equal), string "undefined" vs number, null vs undefined, boolean vs
number.
CASES: Mixed-type comparisons requiring ToNumber/ToPrimitive conversions per ES spec.
RISKS: Bug likely in compareAsNumbers missing NaN handling or compareToUndefined returning incorrect
boolean.
RISKS: Limited to failing test inputs; other operator types (shift, arithmetic) may be indirectly
affected.
RISKS: No runtime context; cannot check interaction with non-constant subtrees or side-effectful
expressions.