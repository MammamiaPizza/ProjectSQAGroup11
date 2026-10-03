TARGETS: EmbeddedRungeKuttaIntegrator step-size adaption (safety, minReduction, maxGrowth);
event-time handling in step loop.
ORACLES: Reference solution; explicit tolerance from manager; assert convergence order via
polynomial test.
CASES: Safety 0.9/1.0/1.1; minReduction 0.0/0.2; maxGrowth 1.0/10.0; forward/backward; event at step
boundary.
RISKS: Cannot inspect Modified class diff; only failing test as oracle; safety config may hide
precision errors.