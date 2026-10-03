TARGETS: CMAESOptimizer.doOptimize and FitnessFunction bound repair/penalty during bounded optimization.
ORACLES: Existing trigger testMath864; result must not exceed upper bound 0.5 (reported failure 0.5246031767455861).
CASES: Bounded objective whose optimum lies at/above an upper bound; assert returned point remains within bounds.
CASES: Lower/upper-bound boundary optimization with configured inputSigma and deterministic RandomGenerator if exposed by inherited API.
RISKS: Stochastic CMA-ES can make numeric assertions flaky; prefer trigger setup and bound invariants.
RISKS: Private FitnessFunction/helpers are not directly testable; available constructor-only context limits configuration details.