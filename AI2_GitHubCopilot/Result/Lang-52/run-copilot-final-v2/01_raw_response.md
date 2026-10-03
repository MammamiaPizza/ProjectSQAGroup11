TARGETS: escapeJavaScript(String) — must escape forward slash before </ to prevent script-tag
breaking.
TARGETS: escapeJavaStyleString(Writer,String,boolean) — underlying private escape method; ensure it
escapes / when escapeSingleQuotes=true.
ORACLES: Test expected: <script>alert('aaa');<\\/script>\\';> for input containing </script>.
ORACLES: Expected behavior: </ in JS string literal becomes <\\/ to avoid premature closing of HTML
script tag.
CASES: Input "foo</script>bar" → "foo<\\/script>bar". Normal case no slash → unchanged.
CASES: Input with unrelated forward slash (e.g., a/b) → slash not escaped. Input null → expect
NullPointerException or documented behavior.
CASES: Input empty → empty return. Input with </ not at start (e.g., x</y) → escaped. Multiple </ →
all escaped.
RISKS: Private escapeJavaStyleString is shared with escapeJava; add slash escaping only for
JavaScript mode to avoid breaking Java escape.
RISKS: Limited context: no source code; rely on test failure message and expected output. Potential
edge: escaping only < + / or any /?