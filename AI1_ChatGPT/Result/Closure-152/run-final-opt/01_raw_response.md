TARGETS: FunctionType methods involved in backwards typedef resolution; inspect casts to ObjectType.  
ORACLES: Existing TypeCheckTest::testBackwardsTypedefUse1/2/3 must complete without ClassCastException.  
CASES: Backwards typedef resolving to StringType; verify no ObjectType-only path is assumed.  
CASES: Backwards typedef resolving to UnionType; cover both reported union-triggering declarations.  
RISKS: Context lacks FunctionType bodies and compiler-input fixtures, limiting direct method-level expectations.