TARGETS: CoreFunction.compute/computeValue, especially functionRound for XPath round(number).
ORACLES: Trigger assertion: evaluating round('NaN') must produce Double.NaN, not 0.0.
CASES: round('NaN') through expression evaluation; assert Double.isNaN on the numeric result.
CASES: Preserve normal round behavior only where existing CoreFunctionTest supplies expected results.
RISKS: NaN comparison requires Double.isNaN, not equality; avoid relying on another project version.
RISKS: Context/setup and function-code constants are not provided; use existing test patterns.