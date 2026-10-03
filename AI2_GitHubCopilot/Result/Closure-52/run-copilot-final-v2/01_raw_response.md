TARGETS: CodeGenerator.isSimpleNumber/getSimpleNumber; property-key serialization in addExpr

ORACLES: Expected var x={["010"]:1} (from testNumericKeys); spec: "010" has leading zero, not a
simple number

CASES: Numeric-key strings "0","1","10","1.0","1e2" vs non-simple
"010","0xA","1.00","Infinity","NaN","00"

RISKS: No full CodeGenerator source; addExpr logic for quoted-key path vs numeric-key path is
inferred