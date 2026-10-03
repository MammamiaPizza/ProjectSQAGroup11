TARGETS: formatLongOctalBytes/parseOctal round-trip; formatOctalBytes for smaller fields.
ORACLES: round-trip equality; IllegalArgumentException for values exceeding field bit capacity.
ORACLES: field bit-max is (length-1)8 bits; documentation/spec defines allowed value range.
CASES: normal:0,±1,±123456; boundary: ±Long.MAX_VALUE, ±2^55, ±2^56-1, ±(2^(88-1)-1), min long.
CASES: error: values >2^((length-1)*8)-1 or < -2^((length-1)*8) should throw.
CASES: negative value -72057594037927935 (8-byte field) must round-trip without error.
RISKS: formatLongOctalBytes may incorrectly reject valid negative values due to signed-range
miscalculation.
RISKS: BigInteger.toByteArray sign extension could create extra bytes causing false overflow.
RISKS: parseOctal for negative values may break if encoding uses sign byte not pure octal digits.