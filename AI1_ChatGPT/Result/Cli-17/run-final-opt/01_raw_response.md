TARGETS: PosixParser.flatten(Options,String[],boolean), burstToken(String,boolean) token bursting/stop behavior.
ORACLES: Existing PosixParserTest.testStopBursting assertion: parsed command line has exactly 2 extra args.
CASES: Burst a multi-option token, then encounter an option requiring an argument; verify remaining suffix handling.
CASES: Repeat trigger with stopAtNonOption true versus false, checking flattened/parsed extra arguments.
CASES: Boundary tokens: "-" and "--"; ensure they are not incorrectly burst or lost.
RISKS: flatten is protected; test may need same-package access or parse-based observable results.
RISKS: No option definitions or exact input from trigger are provided; avoid assuming token semantics.