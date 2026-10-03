TARGETS: GeneratorBase.writeNumber(BigDecimal) path and _asString(BigDecimal) conversion logic.
TARGETS: handling of WRITE_BIGDECIMAL_AS_PLAIN feature during BigDecimal output.
TARGETS: UTF8JsonGenerator and WriterBasedJsonGenerator BigDecimal write methods delegating to
_asString.
ORACLES: TestJsonGeneratorFeatures::testTooBigBigDecimal expects an exception instead of output
1E+10000.
ORACLES: expected-result source is the trigger assertion "should not have written without
exception".
CASES: normal finite BigDecimal values (small exponent) still serialize without error.
CASES: boundary values near the exponent/size limit (e.g. 1E+/-small).
CASES: error case new BigDecimal("1E+10000") with the plain feature enabled must throw.
CASES: same huge value with scientific-notation feature (plain off) — expected behavior unspecified.
RISKS: truncated signatures hide the exact threshold constant and exception type; only one
triggering test is visible.