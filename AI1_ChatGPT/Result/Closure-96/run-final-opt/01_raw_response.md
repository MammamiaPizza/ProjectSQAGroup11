TARGETS: TypeCheck.process/processForTesting/check traversal, especially visitCall function-argument checking.
ORACLES: TypeCheckTest::testFunctionArguments16 requires a warning; exact diagnostic is not provided.
CASES: Reproduce the trigger's function-call argument pattern and assert warning reporting.
CASES: Include valid argument calls, mismatched arguments, and boundary arity where supported by the fixture.
RISKS: Trigger source, expected warning text/type, and surrounding compiler setup are unavailable.