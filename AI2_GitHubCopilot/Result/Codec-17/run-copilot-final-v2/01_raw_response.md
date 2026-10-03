TARGETS: StringUtils.newString(byte[],String); private newString(byte[],Charset); delegates
newStringUtf8/Iso8859_1/UsAscii/Utf16*/Utf16Be/Utf16Le.
ORACLES: CODEC-229 contract: every public newString* variant must return null for a null byte[]
input instead of throwing NullPointerException.
CASES: pass null to each public newString* variant; empty byte[] should yield empty string; non-null
arrays round-trip via matching getBytes*.
RISKS: private newString(byte[],Charset) is not directly testable; expected null behavior is
inferred from the CODEC-229 test name only.
RISKS: Avoid asserting UnsupportedEncodingException/unknown-charset behavior; it is outside the
reported defect.