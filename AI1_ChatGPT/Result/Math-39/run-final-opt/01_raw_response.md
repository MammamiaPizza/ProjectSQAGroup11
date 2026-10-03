TARGETS: EmbeddedRungeKuttaIntegrator.integrate(ExpandableStatefulODE,double), initial-step filtering/last-step logic.
ORACLES: Existing DormandPrince853IntegratorTest.testTooLargeFirstStep assertion and integrator state/time results.
CASES: Too-large first step toward target; verify integration reaches target without the trigger assertion failure.
CASES: Forward and backward target directions around first-step bounds, if supported by existing test fixtures.
RISKS: Abstract target; test through concrete DormandPrince853Integrator and available ODE fixtures.
RISKS: Context omits exact expected values, step-size bounds, and failure assertion details.