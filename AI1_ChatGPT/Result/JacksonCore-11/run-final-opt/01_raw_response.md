TARGETS: addName overloads and rehash growth in ByteQuadsCanonicalizer.  
TARGETS: findName overloads after expansion; size/count and bucket distribution accessors.  
ORACLES: added names must be retrievable by the same quad key; no ArrayIndexOutOfBoundsException.  
ORACLES: size/totalCount should reflect inserted symbols; existing entries survive rehash.  
CASES: add enough distinct byte-symbol quads to force table expansion, then verify all lookups.  
CASES: exercise 1-, 2-, 3-, and long-quad add/find paths around growth boundaries.  
CASES: use collision-heavy keys to cover secondary/tertiary/spillover placement during expansion.  
RISKS: parser-trigger behavior is known, but parser APIs/input construction are not provided here.  
RISKS: exact rehash threshold and collision expectations are not specified by the context.