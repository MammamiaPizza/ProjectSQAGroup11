TARGETS: DSCompiler method computing atan2 derivatives (likely division/reciprocal)
TARGETS: getPartialDerivativeIndex, reciprocal[], combined list for derivative atan2
ORACLES: Java Math.atan2 special-case semantics (e.g., atan2(0,0)=0.0)
ORACLES: Expected derivative for atan2 at (0,0) is 0.0, not NaN
CASES: atan2(0,0) and sign variants: (-0,0),(0,-0),(-0,-0)
CASES: atan2 with one zero argument (y=0,x≠0 or y≠0,x=0)
CASES: Higher-order derivatives at zero arguments
RISKS: Incomplete source of DSCompiler; bug may be in intermediate reciprocal/division
RISKS: Division by zero when x^2+y^2=0 leads to NaN in derivative computation
RISKS: Missing guard for zero denominator before 1/(x^2+y^2) or reciprocal step