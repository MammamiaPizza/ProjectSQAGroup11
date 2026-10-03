TARGETS: MockHandler.handle tracks invocations, used by MockitoCore.verify for verification
matching.
ORACLES: Expected from test name: verify should succeed even if another mock call appears in same
line.
ORACLES: Bug causes AssertionFailedError when verifying with extra different-mock call in
verification expression.
CASES: Normal: verify(mock1).method(mock2.something()) where mock2.something() is extra call.
CASES: Boundary: verify with no extra calls; extra call on same mock; extra call on different mock
after main call.
CASES: Error: verify on mock never called; verify after extra call that throws exception.
CASES: Multiple extra calls to different mocks in same verification line.
RISKS: Only single-threaded behavior considered; thread-safety of MockingProgress untested.
RISKS: MockHandler internals like setAnswersForStubbing may not be triggered by this scenario.
RISKS: The bug fix may affect only MockHandler and MockitoCore; test limited to those classes.