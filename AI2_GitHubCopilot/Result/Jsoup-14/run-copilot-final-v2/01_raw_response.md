TARGETS: TokeniserState.Textarea.read(), TokeniserState.Title.read() handling of unterminated/an
unclosed tags
ORACLES: Unterminated textarea must not consume subsequent <p> into its content; unclosed title must
stop at </head> or EOF
ORACLES: Expected outputs from trigger tests: "one" only (no "<p>two") for
parsesUnterminatedTextarea; "One" only for handlesUnclosedTitle
CASES: Normal: <textarea>text</textarea>, <title>text</title>; Unterminated: <textarea>text<p>;
Unclosed: <title>text<b>bold</title>
CASES: Boundary: empty <textarea></textarea>; <title></title>; only whitespace inside; deeply nested
tags inside unclosed title
RISKS: TokeniserState is private enum; must test through Parser.parse / Tokeniser to inspect token
stream or resulting DOM
RISKS: Only buggy version available; cannot diff fixed code; rely on known failure symptoms and
expected outputs from bug report