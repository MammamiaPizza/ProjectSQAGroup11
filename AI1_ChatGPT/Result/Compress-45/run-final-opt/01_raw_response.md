TARGETS: TarUtils.formatOctalBytes, formatLongOctalBytes, and parseOctal round-trip behavior for 8-byte fields.
ORACLES: parseOctal result after formatting; trigger expects -72057594037927935 to fit an 8-byte field.
CASES: 8-byte round trip for -72057594037927935; verify returned write length and parsed value.
CASES: Boundary negative/positive values representable by the long-octal/binary encoding path.
CASES: Values too large for an 8-byte field should retain documented IllegalArgumentException behavior if applicable.
RISKS: Only trigger failure and signatures are provided; exact field encoding/byte-layout expectations are unavailable.