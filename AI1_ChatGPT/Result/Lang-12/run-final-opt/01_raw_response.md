TARGETS: random(int,int,int,boolean,boolean,char...) range handling when chars is supplied.  
TARGETS: random overloads delegating to the core generator; negative count and empty character inputs.  
ORACLES: Invalid count/range/character-array inputs should raise IllegalArgumentException, not AIOOBE.  
ORACLES: Valid requested count returns a string of exactly that length.  
CASES: chars empty; chars nonempty with end greater than chars.length; start/end boundary values.  
CASES: count 0, negative count, and normal valid subrange into supplied chars.  
RISKS: Random output is nondeterministic; assert length, allowed characters, and exception type only.  
RISKS: Exact validation messages and test source details are unavailable in this context.