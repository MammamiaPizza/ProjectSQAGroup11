TARGETS: ListPopulation.iterator() iterator mutability/removal behavior.  
ORACLES: MATH-779 trigger expects Iterator.remove() to throw UnsupportedOperationException.  
CASES: Create concrete ListPopulation with chromosome(s); call iterator().remove() and assert UOE.  
CASES: Verify iteration still returns contained chromosomes and preserves population contents.  
RISKS: ListPopulation is abstract; test needs an existing concrete test subclass.  
RISKS: No other-version context; avoid asserting unspecified iterator ordering or null behavior.