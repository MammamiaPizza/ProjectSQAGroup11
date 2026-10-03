TARGETS: escapeJavaScript(String), escapeJavaScript(Writer,String); Java-style slash escaping.
ORACLES: Trigger comparison: "</script>" within JS text must produce "<\\/script>".
CASES: Normal JS "alert('aaa');</script>'" expects escaped apostrophes and escaped slash.
CASES: Boundary: slash alone, "</", and multiple slashes verify each slash is escaped for JavaScript.
CASES: Writer overload output should equal String overload for identical input.
RISKS: EscapeJava behavior may differ; scope assertions to escapeJavaScript only.
RISKS: No source/body context supplied; avoid assumptions on null, Unicode, or IOException behavior.