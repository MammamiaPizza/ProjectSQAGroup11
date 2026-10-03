TARGETS: resolve(Type,Class,Type) and resolveTypeVariable for inherited generic collection element types  
TARGETS: getCollectionElementType(context, rawType) feeding collection deserialization type selection  
ORACLES: Trigger testIssue1107: collection elements must deserialize as SmallClass, not LinkedTreeMap  
ORACLES: Reflection Type results from declared generic collection fields/supertypes  
CASES: Parameterized collection whose element type is a concrete class through generic inheritance  
CASES: Direct parameterized Collection<SmallClass> resolution as normal baseline  
CASES: Unresolved type variable and raw Collection boundaries should retain Object fallback behavior  
RISKS: Internal/package-private helpers may require tests in com.google.gson.internal  
RISKS: Context lacks the exact generic declarations used by testIssue1107 and intended fix details