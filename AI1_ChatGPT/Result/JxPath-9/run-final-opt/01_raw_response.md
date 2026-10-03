TARGETS: CoreOperationEqual/NotEqual computeValue; CoreOperationCompare equal, contains, findMatch.  
ORACLES: CoreOperationTest::testNan: <$nan = $nan> must evaluate to false.  
ORACLES: Equality/inequality behavior is exposed through expression evaluation and Boolean computeValue results.  
CASES: NaN compared with NaN using equality; assert false.  
CASES: NaN compared with NaN using inequality; assert complementary true behavior if expression syntax supports it.  
CASES: Exercise iterator-based matching/containment when one or both compared values are NaN.  
RISKS: Protected comparison helpers require same-package/subclass access or indirect expression evaluation.  
RISKS: No further expected results for nulls, numbers, strings, or collections are provided in context.