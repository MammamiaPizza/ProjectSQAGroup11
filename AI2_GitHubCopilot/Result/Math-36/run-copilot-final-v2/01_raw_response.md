TARGETS: BigFraction.floatValue(), BigFraction.doubleValue()
ORACLES: expected result computed via BigDecimal division of numerator by denominator with
sufficient precision
CASES: large num/den where BigInteger.doubleValue() overflows, fraction is finite (e.g., exactly
5.0)
CASES: fraction near Double.MAX_VALUE, Double.MIN_VALUE, zero, one, negative values
RISKS: cannot inspect buggy conversion logic; avoid using the method under test to derive expected
values
RISKS: existing test failures show NaN for large finite fractions; all large-value tests must use
mathematical oracle