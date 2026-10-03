TARGETS: LiveVariablesAnalysis flow-through handling for FOR...IN loop's object expression
(addToSetIfLocal, getVarIndex)
ORACLES: no IllegalStateException; check live-set via isLive(index) or isLive(var) against expected
def-use
CASES: for/in over simple local var, over property access (e.g., obj.prop), over function call,
nested for/in, non-local var
RISKS: missing var registration for object expression leads to index out-of-bound or
IllegalStateException in getVarIndex/addToSetIfLocal