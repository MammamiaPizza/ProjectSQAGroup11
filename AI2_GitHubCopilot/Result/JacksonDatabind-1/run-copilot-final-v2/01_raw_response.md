TARGETS: serializeAsColumn, serializeAsPlaceholder; _nullSerializer, assignNullSerializer,
MARKER_FOR_EMPTY
ORACLES: testNullColumn expected "<[null,[]"bar"]>": null property → single null token, not
duplicated
CASES: non-null value; null value (nullSerializer null/set); boundary: null with
_suppressNulls=true; empty object
RISKS: internal serialization logic invisible; exact role of placeholder vs column in doubling nulls
uncertain