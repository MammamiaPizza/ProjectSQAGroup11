TARGETS: parseOctal: handle 0xff leading byte (negative flag) and octal string to long conversion;
verifyCheckSum: header sum validation.
TARGETS: parseName: extract NUL-terminated name for magic/version archive detection.
ORACLES: POSIX ustar: octal field 12 bytes padded NUL/space; 0xff first byte -> negative 2's comp;
checksum field as 8 spaces.
CASES: parseOctal: normal 8-digit octal; max 12-digit; 0xff first byte; trailing spaces; non-octal
char -> IllegalArgumentException.
CASES: verifyCheckSum: matching checksum; checksum field all spaces (zero sum); mismatch; sum
overflow wrap-around.
CASES: parseBoolean: zero/non-zero byte (possible detection use); parseName with embedded NULs.
RISKS: No access to failure-inducing tar file or DetectArchiver logic; must infer from spec and
modified class.
RISKS: Negative parseOctal behavior for 0xff may be the root cause; unclear if detection depends on
boolean or name.