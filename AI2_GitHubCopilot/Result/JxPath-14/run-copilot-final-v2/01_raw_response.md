TARGETS: CoreFunction.functionRound(EvalContext) NaN handling
TARGETS: CoreFunction.compute/computeValue dispatch to round case
ORACLES: JXPATH-102: round('NaN') must return NaN, not 0.0
ORACLES: XPath 1.0 round spec (NaN in, NaN out)
CASES: round arg resolving to Double.NaN via number('NaN')
CASES: round normal .5/.49 and negative tie boundaries
CASES: round on positive/negative Infinity inputs
CASES: wrong arg count for round (assertArgCount path)
RISKS: prompt lacks official expected for -0.0 and tie direction
RISKS: protected functionRound may need helper/test in same package