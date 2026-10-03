TARGETS: createNumber(String), isNumber(String), createLong(String), createInteger(String)
ORACLES: LANG-300 bug: "1l" must not throw; isNumber true ⇒ createNumber success; Java parseXxx for
plain strings
CASES: normal: "1l","1L","2l","0l","-1l","123l"; boundary: "2147483648l","l"; error:
null,"","1.0l","1f"
RISKS: No formal spec; only bug report assures 'l' handled as 'L'; behavior for decimal+'l' unknown;
limited context