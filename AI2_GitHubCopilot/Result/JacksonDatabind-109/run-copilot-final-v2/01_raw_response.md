TARGETS: NumberSerializer.serialize for BigDecimal input values
TARGETS: NumberSerializer._isInt field (false for BigDecimal class)
ORACLES: Expected JSON uses plain string per WRITE_BIGDECIMAL_AS_PLAIN
CASES: Normal: 0.1, 100; Boundary: 0.0000000005, 1E-20, -1E-20
CASES: Feature off: output should keep exponent notation
CASES: BigInteger values (scaled integers) should not switch to plain
CASES: Large integer-like BigDecimal (1E+20) also plain output
CASES: Zero, negative zero
CASES: null Number, non-BigDecimal Number
RISKS: WRITE_BIGDECIMAL_AS_PLAIN feature assumed from Jackson API