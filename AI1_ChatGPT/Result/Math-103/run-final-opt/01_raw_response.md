TARGETS: NormalDistributionImpl.inverseCumulativeProbability(p), cumulativeProbability(x), domain-bound helpers.  
ORACLES: Existing NormalDistributionTest::testExtremeValues and distribution inverse/CDF consistency.  
CASES: Extreme finite mean/sd with probabilities whose inverse search previously exceeds 10,000 iterations.  
CASES: Default and parameterized distributions; p near 0 and 1 plus representative interior p values.  
ORACLES: inverse result x should yield cumulativeProbability(x) consistent with requested p within test tolerance.  
RISKS: Boundary p=0/1 and invalid p behavior are unspecified here; avoid assuming exception/endpoint semantics.  
RISKS: Protected helper methods need subclass exposure if directly tested; prefer public inverse behavior.  
RISKS: No fixed expected numeric values or exact tolerances supplied beyond existing trigger context.