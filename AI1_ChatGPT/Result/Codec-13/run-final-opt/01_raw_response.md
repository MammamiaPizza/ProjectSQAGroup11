TARGETS: DoubleMetaphone.isDoubleMetaphoneEqual overloads, doubleMetaphone, and StringUtils/CharSequenceUtils equality.  
ORACLES: Existing DoubleMetaphoneTest expectations: testIsDoubleMetaphoneEqualBasic and testCodec184.  
CASES: Equal and unequal ordinary names through primary and alternate equality overloads.  
CASES: Null arguments to equality and encoding paths implicated by CODEC-184; assert no unexpected NPE.  
CASES: Same-reference, one-null, and both-null CharSequence/String equality boundary cases.  
RISKS: CharSequenceUtils API details are truncated; limit tests to available public behavior and trigger evidence.