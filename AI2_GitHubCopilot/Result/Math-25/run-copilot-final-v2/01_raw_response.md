TARGETS: fit() methods; ParameterGuesser.guess(), guessAOmega(), guessPhi(), sortObservations().
Exception-throwing paths.
ORACLES: Existing testMath844 expectation; API docs on MathIllegalStateException; optimizer
convergence contract.
CASES: Insufficient points (0,1,2); constant/identical y; observations causing guess failure (no
freq range); exact testMath844 data.
CASES: Boundary with 3 points; invalid initial guess; non-converging optimizer settings.
RISKS: Cannot inspect trigger test code—exact exception condition inferred from summary; actual
fitter internals may vary.