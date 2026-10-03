TARGETS: EmbeddedRungeKuttaIntegrator integration step/event handling; safety, minReduction, maxGrowth accessors.
ORACLES: AdamsMoultonIntegratorTest::polynomial assertion behavior is the only supplied expected-result source.
CASES: Polynomial ODE integration through normal steps and event-truncated steps, forward and backward directions.
CASES: Accessor round trips for set/getSafety, set/getMinReduction, and set/getMaxGrowth.
RISKS: Trigger is in AdamsMoultonIntegratorTest; no concrete EmbeddedRungeKuttaIntegrator subclass/API details supplied.
RISKS: Exact polynomial equation, interval, tolerances, and expected values are absent; do not infer them.