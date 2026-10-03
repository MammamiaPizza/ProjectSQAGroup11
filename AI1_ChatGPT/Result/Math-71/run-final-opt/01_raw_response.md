TARGETS: RungeKuttaIntegrator and EmbeddedRungeKuttaIntegrator integration event handling near final time.
ORACLES: Existing testMissedEndEvent expected event time 1.8782503799999986E9.
CASES: ClassicalRungeKuttaIntegrator missed-end event; assert event time is not advanced by a step.
CASES: DormandPrince853Integrator missed-end event; assert the same expected event time.
CASES: Forward integration where manager event time lies between stepStart and proposed nextT.
CASES: Event at/near target end time; verify final-step/event selection behavior.
RISKS: Only trigger outcomes and partial implementation context are supplied; event setup API is not shown.