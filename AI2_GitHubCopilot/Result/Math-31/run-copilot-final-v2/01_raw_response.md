TARGETS: evaluate(x,epsilon,maxIterations) convergence loop; scale = FastMath.max(a,b) stability;
NaN detection or generation
ORACLES: Finite result for convergent inputs; ConvergenceException (not NaN propagation) on
divergence; tolerances honored
CASES: x=0.5 (known NaN trigger), x near poles/singularities, epsilon tight/loose, maxIterations
low/high, a==0 or b==0 components
RISKS: Concrete subclass behavior unknown; NaN may be buried in intermediate steps before scale is
applied; exact fix strategy unclear