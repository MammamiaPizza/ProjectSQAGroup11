TARGETS: getLeastSupertype(JSType) union logic; isSubtype(JSType) property subset; isEquivalentTo
ORACLES: bug report expected union strings; assert isSubtype(result, arg1) && isSubtype(result,
arg2)
CASES: overlapping non-subtype records; disjoint; identical; subset (one narrower); empty records;
null that
RISKS: RecordType construction needs JSTypeRegistry; UnionType creation opaque; internal property
map is hidden