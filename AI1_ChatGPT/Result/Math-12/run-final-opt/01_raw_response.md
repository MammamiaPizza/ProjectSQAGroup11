TARGETS: BitsStreamGenerator.nextGaussian() cached-value behavior and clear() state reset.  
TARGETS: nextDouble(), next(int), and seed methods as sources of Gaussian sequence state.  
ORACLES: Distribution clone tests require matching sampled values after cloning.  
ORACLES: Triggered expected values identify deterministic sequence mismatches.  
CASES: Consecutive nextGaussian() calls, including cached second variate consumption.  
CASES: clear() before nextGaussian(), then verify no stale cached Gaussian is reused.  
CASES: Seed/reset followed by Gaussian sampling where subclass behavior permits determinism.  
RISKS: next(int) is abstract; direct tests need a deterministic concrete generator/test subclass.  
RISKS: Available context does not state exact seed or clone contracts beyond trigger failures.