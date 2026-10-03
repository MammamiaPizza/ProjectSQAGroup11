TARGETS: RecordType/JSType subtype behavior with unknown types; UnionType subtype/equivalence interactions.  
TARGETS: ArrowType and FunctionType subtype, least-supertype, greatest-subtype, equality behavior.  
ORACLES: Existing triggers TypeCheckTest::testIssue791 and RecordTypeTest::testSubtypeWithUnknowns2.  
CASES: Record subtype checks where required property types include unknown versus concrete types.  
CASES: Type-check code path from issue 791; assert no unexpected warnings using existing test harness.  
CASES: Union/record/function comparisons involving unknown alternates and subtype direction.  
RISKS: Available signatures lack constructors/factories and exact expected subtype outcomes.  
RISKS: Do not infer semantics beyond trigger assertions and existing test utilities.