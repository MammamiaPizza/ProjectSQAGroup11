TARGETS: escapeToDoubleQuotedJsString (CodeGenerator)
ORACLES: testZero expects null char -> "\000" (octal) not "\0"
CASES: null-only, null+digit, null at start/end/mid, multiple nulls, borderline chars
RISKS: other escape methods may need same fix; limited to supplied bug summary