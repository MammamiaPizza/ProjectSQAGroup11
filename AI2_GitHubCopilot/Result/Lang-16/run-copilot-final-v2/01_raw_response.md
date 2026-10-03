TARGETS NumberUtils.createNumber hex-parsing for 0x/0X prefixes.
ORACLES valid hex (e.g. "0x1a","0Xfade") returns Number; value equals Long.decode or Integer.decode.
ORACLES invalid hex ("0x","0XGHI",null) must throw NumberFormatException.
CASES normal: "0x1a","0Xfade","0x0","0X0","-0xF".
CASES boundary: "0x7fffffffffffffff" (Long max), "0X8000000000000000" likely throws.
CASES large hex > int: "0xffffffff" should be Long (not negative Integer).
CASES errors: only prefix ("0x","0X"), malformed ("0xGHI","0x "), empty, null.
RISKS only createNumber hex is buggy; other parsers (isNumber,createInteger) are unaffected.
RISKS no spec for octal/binary; only hex (0x/0X) fix expected.
RISKS undefined behavior for "0x"-prefixed text with spaces or trailing garbage beyond first bad
char.