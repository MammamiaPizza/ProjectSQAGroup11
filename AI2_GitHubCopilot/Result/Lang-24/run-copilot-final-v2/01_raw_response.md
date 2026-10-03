TARGETS: isNumber(String) — validate valid/invalid number strings per Java number formats.
ORACLES: True when string is valid hex/octal/decimal/scientific notation; false otherwise. Compare
with decode/valueOf parsing.
ORACLES: Null or blank string must return false; no exceptions thrown.
ORACLES: Follow existing isNumber contract: support 0x, #, e/E, +/-, decimal point, suffixes
L/f/d/D.
CASES: Normal: "123", "-456", "0", "1.5", "1e5", "-1E-10", "0x1A", "#FF", "007".
CASES: Normal: "1L", "1f", "1d", "+42", "+0xA", "0.0", "0001".
CASES: Boundary/Error: "", null, " ", "abc", "1 2", "1.2.3", "0x", "1e", "1e1.1", "--1".
CASES: Boundary: leading zeros like "0.0", "-000", trailing dot "1.", leading dot ".5".
RISKS: Cannot see testIsNumber expectations; rely on LANG-664 and isNumber Javadoc only.
RISKS: isNumber used internally by createNumber; any regression must be verified with existing test
suite.