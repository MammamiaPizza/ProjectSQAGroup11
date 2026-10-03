TARGETS: NumberUtils.createNumber(String)
ORACLES: Should return a Long (or Integer) without throwing NumberFormatException for valid decimal
strings
CASES: "80000000" (trigger), boundary-ints like "2147483648" (exceeds int), Long.MAX_VALUE, "0",
"-1"
CASES: Strings with hex-like digits but no prefix (e.g., "0A", "0x1A" prefix), octal-like "0777",
leading zeros
RISKS: Cannot see full hex_prefixes loop; guess that octal/hex-misidentification causes the
exception
RISKS: Expected type (Integer/Long) for large values unclear; must only test that no exception
occurs