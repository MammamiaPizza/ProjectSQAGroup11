TARGETS: TarUtils.parseOctal(byte[], int, int) - trailing space/NUL handling
ORACLES: POSIX tar: skip leading/trailing space (0x20) and NUL (0x00); treat empty field as 0
ORACLES: Must not throw on valid archive bytes, e.g., space-padded octal
CASES: " \0" (len 2) → 0; trailing spaces after octal digits; leading spaces before digits
CASES: All-NUL or all-space fields; mixed spaces/NULs around octal; max-long octal padded
CASES: Length 0; offset+len out of bounds; non-octal chars beyond allowed padding
RISKS: Bug throws IllegalArgumentException on benign tar headers; fix may break edge parsing
RISKS: Risk of silent misparse if fix allows invalid characters; need regression tests for known
archives