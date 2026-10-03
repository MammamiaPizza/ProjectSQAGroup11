TARGETS: ClosureReverseAbstractInterpreter.caseTopType must narrow null/void for
goog.isArray/Function/Object.
ORACLES: Expected type from JSTypeRegistry: native ARRAY/FUNCTION/OBJECT types; check
intersect/exclusion.
CASES: goog.isArray on null (false outcome) → type stays null; on void (undefined) → void; on
null|Array union.
CASES: goog.isFunction on null → null; goog.isObject on null → null; ensure no regression for
non-null inputs.
RISKS: Changing caseTopType may affect other goog.is* predicates; must only add handling for
Array/Function/Object.