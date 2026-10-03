TARGETS: CoalesceVariableNames.visit name-coalescing; JsDocInfoParser license extraction with
annotations
ORACLES: Expected error messages in failing tests; DocInfo assertions; existing unit tests;
coalescing must not merge vars with overlapping live ranges
CASES: Vars with same name in different scopes; License comment containing @type/@param; boundary:
empty license block, trailing whitespace
RISKS: No implementation details; failure clues only; cannot reproduce; need to validate both
modified classes together