TARGETS: TarUtils.parseOctal(byte[], int, int), especially 12-byte octal fields.
ORACLES: Trigger expects "777777777777" to parse without IllegalArgumentException.
CASES: Parse maximal all-'7' 12-byte field; assert returned long value.
CASES: Valid octal digits with leading NUL/space and bounded offset/length.
CASES: Invalid non-octal byte still raises IllegalArgumentException.
RISKS: Exact numeric expectation is not supplied; derive only from documented field parsing behavior.
RISKS: No source diff or alternate version is available; avoid assumptions about unrelated methods.