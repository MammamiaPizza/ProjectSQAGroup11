TARGETS: CheckGlobalThis.shouldTraverse, visit, shouldReportThis for issue-182 source patterns.
TARGETS: RuntimeTypeCheck.process and AddChecks handling of values with inner functions.
ORACLES: CheckGlobalThisTest.testIssue182a/b require exactly one reported error each.
ORACLES: RuntimeTypeCheckTest.testValueWithInnerFn is the expected-result source for inner-function values.
CASES: Exercise global-this reporting paths represented by issue-182a and issue-182b.
CASES: Exercise runtime type-check insertion for a value containing an inner function.
RISKS: Available context lacks the trigger JavaScript inputs and exact diagnostic/type-check assertions.