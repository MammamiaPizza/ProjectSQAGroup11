TARGETS: StopWatch suspend(), resume(), stop(), getTime() state/time accounting  
ORACLES: Existing StopWatchTest::testLang315 assertion failure and public getTime() results  
CASES: start→suspend→resume→stop should exclude suspended interval  
CASES: Repeated suspend/resume cycles; check elapsed time continuity and accumulation  
CASES: Boundary: immediate suspend/resume/stop; elapsed time remains nonnegative  
RISKS: Time-based assertions are flaky; use tolerances and sufficiently distinct delays  
RISKS: Context lacks state-transition exception behavior and exact expected timing values