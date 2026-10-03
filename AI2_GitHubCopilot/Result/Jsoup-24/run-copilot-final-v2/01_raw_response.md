TARGETS: TokeniserState.ScriptDataEscapedDashDash.read, ScriptDataEscapedDash.read, anythingElse
helper
TARGETS: ScriptDataEscaped.read – handling of quotes within // and <!-- style script comments
ORACLES: HtmlParserTest.assert: output contains document.write('</scr['] + 'ipt>'); – comment quote
preserved
CASES: Normal: single/double quotes inside script comments after // or <!--
CASES: Boundary: comment with quote at start/end, quote before </script>, empty comment, consecutive
quotes
CASES: Error: unclosed comment containing quote, nested comment-like tokens after quote
RISKS: Limited to test assertion; no full state-machine diff; fix may involve multiple
TokeniserState constants