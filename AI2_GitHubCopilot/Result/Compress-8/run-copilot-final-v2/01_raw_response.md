TARGETS: parseOctal validation of length (should throw IllegalArgumentException if <2 bytes).
ORACLES: bug report expects IllegalArgumentException for length <2; also parseOctal must reject
non-octal chars.
CASES: length=0,1,2 (no chars); valid octal parsing; invalid char at offset; offset+length > buffer;
null buffer.
RISKS: only buggy version available; no spec beyond trigger test; expected behavior inferred from
COMPRESS-113.