TARGETS: parseBigDecimal(String) & parseBigDecimal(char[],int,int) for NaN/Inf inputs;
TextBuffer.contentsAsDecimal()
TARGETS: NumberInput.parseDouble for NaN/Inf token strings; parseDouble may also feed BigDecimal
conversion
ORACLES: For non-finite numbers (NaN, ±Infinity), parseBigDecimal must throw NumberFormatException
with message containing "can not be represented as BigDecimal"
ORACLES: Normal finite numeric strings must parse to correct BigDecimal without exception
CASES: NaN, +Infinity, -Infinity, +Inf, -Inf, 0, "2.2250738585072012e-308", MAX_VALUE, MIN_VALUE,
"-0.0"
CASES: Valid large exponents (e.g., "1e308") and subnormal boundaries
RISKS: The test expects exception on non‑standard tokens; buggy code may return null/NPE instead of
throwing
RISKS: Only BigDecimal path is triggered; parseDouble return value for NaN is not tested for
exception
RISKS: Tokenizers/parser configurations enabling ALLOW_NON_NUMERIC_NUMBERS may bypass this path