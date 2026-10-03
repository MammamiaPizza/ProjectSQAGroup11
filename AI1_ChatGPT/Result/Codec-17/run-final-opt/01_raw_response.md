TARGETS: StringUtils newStringUtf8 and other public newString* byte[] wrappers; null-byte handling is implicated.
ORACLES: Trigger StringUtilsTest::testNewStringNullInput_CODEC229; reported behavior is NPE on null input.
CASES: null byte[] for newStringUtf8; null for ISO-8859-1, US-ASCII, UTF-16/BE/LE wrappers.
CASES: normal ASCII and non-ASCII byte arrays decoded with each named charset wrapper.
CASES: newString(byte[], String): valid charset name, null bytes, and unsupported charset error path.
RISKS: Private newString(byte[], Charset) is the likely shared implementation but cannot be invoked directly.
RISKS: No source/test assertions beyond the trigger are provided; avoid assuming exact null return/value semantics.