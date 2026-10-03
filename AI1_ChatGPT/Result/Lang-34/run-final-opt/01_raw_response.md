TARGETS: ToStringStyle registry lifecycle: getRegistry, register, unregister, isRegistered.  
TARGETS: append/appendInternal cycle detection for objects, object arrays, and primitive arrays.  
ORACLES: Trigger assertions: registry must be null/cleared after builder/reflection rendering, not {}.  
ORACLES: Existing ToStringBuilderTest expected strings and registry assertions are the behavior source.  
CASES: Normal render of noncyclic object and each primitive/object array leaves no registry entry.  
CASES: Self-reference, two-field self-reference, nested/cyclic arrays, and object-array/object cycles.  
CASES: Reflection hierarchy and mixed object-array cycles; assert cleanup after completion.  
RISKS: Registry APIs are package-private; tests may need same package or indirect builder coverage.  
RISKS: Provided context truncates exact output formats and full modified implementation.