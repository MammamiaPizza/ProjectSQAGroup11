TARGETS: BrentOptimizer.optimize convergence on sin(0..2π) yields min~4.712389, quintic yields
min~-0.271956.
ORACLES: Exact minima known analytically: sin min = 3π/2 ≈ 4.71238898038469, quintic min ≈
-0.2719561270319131.
ORACLES: Statistics from repeated runs: mean evaluations = 1880.5 (testQuinticMinStatistics).
CASES: Normal intervals covering minima; tight bounds near minima; start values far from/at
endpoints.
CASES: Multi-start optimizer runs to validate accumulated evaluation counts and convergence across
restarts.
RISKS: Tiny absolute tolerance may cause platform-dependent precision failures; use relative error
check with reasonable epsilon.