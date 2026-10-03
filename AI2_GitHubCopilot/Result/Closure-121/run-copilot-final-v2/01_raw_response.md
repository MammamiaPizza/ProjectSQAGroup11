TARGETS: ​​InlineVariables.process; afterExitScope; doInlinesForScope; isValidInitialization;
isValidReference; canMoveAggressively; isVarInlineForbidden
ORACLES: ​​Compare compiler output string to expected; check variable declarations persist if not
safe; verify no repeated side‑effect evaluation
CASES: ​​Normal constant‑init var; var initialized to side‑effect (function call) used twice; var in
conditional; alias to param; var used as l‑value
RISKS: ​​Unknown Issue1053 trigger code; must infer from API; may miss exact failing condition; cann
examine test or fix