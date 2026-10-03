TARGETS: getIntValue(), getLongValue() overflow detection in TreeTraversingParser
ORACLES: Throw IOException/JsonParseException when number exceeds int/long range (test expects
failure)
CASES: Int overflow: values >Integer.MAX_VALUE, <Integer.MIN_VALUE; Long overflow: >Long.MAX_VALUE,
<Long.MIN_VALUE
CASES: Normal in-range values; boundary values (MAX_VALUE, MIN_VALUE); zero; negative
CASES: Very large BigInteger/BigDecimal beyond long; decimal truncation
RISKS: Exact exception type ambiguous from context; must preserve valid-range behavior
RISKS: No source for overflow-checking algorithm; limited to buggy-version signatures only