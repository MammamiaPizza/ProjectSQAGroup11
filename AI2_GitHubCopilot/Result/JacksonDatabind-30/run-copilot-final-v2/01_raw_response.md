TARGETS: ObjectMapper.writeValue/readValue round-trip with TokenBuffer, external type id for
BigDecimal
TARGETS: TokenBuffer.writeNumber(BigDecimal) must preserve unscaledValue and scale
TARGETS: TokenBuffer.serialize(JsonGenerator) must write BigDecimal faithfully
ORACLES: Assert round-tripped BigDecimal.equals(original) (same value and scale)
CASES: Normal: BigDecimal like -10000000000.0000000001 (high precision)
CASES: Boundary: BigDecimal with extreme scale (0, large positive/negative)
CASES: Boundary: BigDecimal values that could be confused with scientific notation (e.g., 1E+2)
RISKS: Cannot see TokenBuffer internal token storage; may use toString() instead of toPlainString()
RISKS: External id configuration and full TokenBuffer number-handling logic not provided