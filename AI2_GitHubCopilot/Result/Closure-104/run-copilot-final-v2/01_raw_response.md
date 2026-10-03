TARGETS: getGreatestSubtype (inherited), isSubtype, canAssignTo, getLeastSupertype,
getRestrictedUnion, getTypesUnderEquality
ORACLES: Compare returned JSType to expected NoObject/Nne constants; assert true/false from
issubtype for known pairs; check canAssignTo against expected booleans
CASES: Union of NoObject and None, two object-tye unions, union with null, single-alternate union,
empty union, union containing UnknownType
RISKS: getGreatestSubtype not in provided UnionTyp API; JSType constants may be hidden; exact
expected outcomes need exploratn; TypePair members not shown