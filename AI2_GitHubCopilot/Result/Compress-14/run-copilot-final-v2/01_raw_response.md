TARGETS: TarUtils.parseOctal(byte[], int, int) — parsing of tar header numeric fields
ORACLES: Posix ustar spec: leading/trailing spaces and NUL are ignored in octal fields
ORACLES: workaroundForBrokenTimeHeader expects no IOException; parseOctal must handle broken fields
CASES: octal with leading spaces, trailing spaces, trailing NUL bytes, all spaces (expect 0)
CASES: valid octal near Long.MAX_VALUE; 0-length field; offset+length beyond buffer
CASES: field with embedded non-octal char (should throw); field containing only NUL
RISKS: parseBoolean may have similar leniency issues but not triggered by this bug report
RISKS: Without exact source, expected behavior on mixed spaces within octal is uncertain