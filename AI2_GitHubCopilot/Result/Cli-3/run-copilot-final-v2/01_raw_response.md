TARGETS: TypeHandler.createNumber(String) return type/subclass selection
TARGETS: TypeHandler.createValue(String, Object) type coercion fallback
TARGETS: TypeHandler.createValue(String, Class) dispatch to createNumber
ORACLES: Numeric equality of returned value vs expected Number subclass
ORACLES: JUnit assertEquals behavior on distinct Number subclasses (Double vs Float)
CASES: Boundary: "4.5" → Double? Float? — which subtype should represent a decimal literal
CASES: Normal: "1.0", "0.5", "2.5" — all trigger same subclass ambiguity
CASES: Integer strings "5", "0" — may bypass the bug if integral
RISKS: Without source, exact fix (cast/type-promotion rule) is unknown
RISKS: createFile/createFiles/createDate/createClass are untriggered but co-modified