TARGETS: escapeJava(String)
TARGETS: escapeJavaScript(String) (shares private escapeJavaStyleString)
ORACLES: '/' must not be escaped per Java string literal spec; LANG-421 expects no slash escaping.
CASES: "/", "a/b/c", "/", ""\/"", "\\/", "", "\", "/\/", mix with quotes
CASES: null input, empty, slash-only, slash at boundaries, round-trip escape→unescape
RISKS: Changing escape behavior may affect JavaScript escaping; private method hidden
RISKS: UnescapeJava must accept old escaped slash (\/) to support legacy data?