TARGETS: TokeniserState.AttributeName.read; handling '/' in attr name to drop it and set
self-closing.
TARGETS: State transitions upon '/' after attribute name: emit token with correct name, no
truncation.
ORACLES: Test name "dropsSlashFromAttributeName" expects slash removal from trailing attr name.
ORACLES: Failure msg: SelfClosingStartTag ignores last character → attr name must not lose last
char.
CASES: Normal attr name without trailing slash; verify full name emitted.
CASES: Attr name before "/>" (self-closing): name must exclude slash, e.g., "src" not "src/".
CASES: Attr name="value" before "/>": slash is part of value, not name; value unchanged.
CASES: Attr name empty before "/>" (e.g., " /"): name empty, self-closing flag set.
RISKS: Enum constants & dispatch not visible; must isolate AttributeName via Tokeniser input
simulation.
RISKS: Verify that only slash at end of attribute name triggers self-close; mid-name slash remains.