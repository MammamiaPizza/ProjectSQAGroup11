TARGETS: TypeCheck.process/processForTesting traversal and private doPercentTypedAccounting behavior.  
ORACLES: Existing triggers expect typed-percent 100.0 for testGetTypedPercent5 and testGetTypedPercent6.  
CASES: Cover GETPROP/GETELEM expressions whose types should count as typed during traversal.  
CASES: Include nested property/index access and assignment/use contexts reflected by the two trigger tests.  
CASES: Assert exact percent accounting, including numerator/denominator effects for all visited relevant nodes.  
RISKS: Private accounting method requires exercising through public processForTesting or existing test harness.  
RISKS: Context lacks source diff and full trigger JS inputs; derive cases only from existing TypeCheckTest behavior.