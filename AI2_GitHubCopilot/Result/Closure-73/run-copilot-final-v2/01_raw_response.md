TARGETS: CodeGenerator.jsString - ensure chars like U+007F are escaped as \uXXXX even if encodable.
TARGETS: CodeGenerator.escapeToDoubleQuotedJsString - wraps result and may rely on jsString
escaping.
ORACLES: testUnicode expects "\u007f" for U+007F; JS spec requires escape for control chars
(U+0000–U+001F, U+007F–U+009F) in string literals.
CASES: jsString with U+007F → "\u007f"; U+0001 → "\u0001"; U+001F → "\u001f"; U+0080 → "\u0080";
U+009F → "\u009f"; U+2028 → "\u2028"; U+2029 → "\u2029"; letter 'A' → "A".
RISKS: Encodable but invalid chars like U+2028/U+2029 may be missed; CharsetEncoder may affect
output; supplementary chars unknown.