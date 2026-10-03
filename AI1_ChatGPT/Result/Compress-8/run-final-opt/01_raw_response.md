TARGETS: TarUtils.parseOctal(byte[], int, int), especially minimum valid field length validation.
ORACLES: Trigger requires IllegalArgumentException when parseOctal length is less than 2 bytes.
CASES: Valid octal fields with leading/trailing NUL or space padding return their parsed long value.
CASES: length 0 and 1 fields should throw IllegalArgumentException; include nonzero offsets if buffer permits.
CASES: Invalid non-padding/non-octal bytes should throw IllegalArgumentException.
RISKS: Available context gives no exact expected values/messages for valid parsing or malformed offset/range inputs.