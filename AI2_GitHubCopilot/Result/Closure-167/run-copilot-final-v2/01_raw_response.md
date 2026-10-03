TARGETS: RestrictByTrueInstanceOfResultVisitor.caseTopType/Unknown/Object/Union/Function,
applyCommonRestriction
TARGETS: RestrictByFalseInstanceOfResultVisitor.caseTopType/Unknown/Object/Union/Function,
applyCommonRestriction
ORACLES: TypeCheckTest expects TYPE_MISMATCH warning; JSType.toAnnotationString() matches narrowed
type (not "??")
CASES: union(Object|null) truthy → Object, falsy → null; union(Function|undefined) truthy → Function
CASES: unknown→unknown; all→all; top→top; nested union(union(A,B)|C) flattens
CASES: incompatible target (e.g., null instanceof Number) should not restrict type
RISKS: need mocked FlowScope/TypePair or TypeCheck integration; annotation string format may be
internal