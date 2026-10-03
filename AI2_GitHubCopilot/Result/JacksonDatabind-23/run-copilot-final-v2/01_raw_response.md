TARGETS: serialize() in IntegerSerializer, ShortSerializer, IntLikeSerializer, LongSerializer,
FloatSerializer, DoubleSerializer
ORACLES: testEmptyInclusionScalars expects {} when numeric value is 0; diff shows field omitted for
default/empty values
CASES: zero values (Integer, Long, Short, Byte, Float, Double) with Include.NON_DEFAULT/NON_EMPTY;
boundary: -0.0f, 0.0d, max/min values
CASES: non-zero values with same inclusion to confirm they still serialize; null values; Byte/Short
zero as boundary
RISKS: Cannot inspect base class isEmpty() or inclusion-control logic; only trigger test name and
diff provide oracle
RISKS: Must infer exact inclusion mechanism (annotation/config) from test name; behavior for
NaN/Infinity unknown