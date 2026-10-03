TARGETS: StringUtils.join array overloads exercised by testJoin_ArrayChar and testJoin_Objectarray.  
ORACLES: Existing trigger-test assertions and LANG-703 report; regression must eliminate reported NPEs.  
CASES: Object arrays with ordinary values, null elements, null array, empty array, and char separators.  
RISKS: Join signatures/implementation are truncated; exact expected strings must come from existing tests.