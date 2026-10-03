TARGETS: PosixParser.flatten(Options,String[],boolean) stopAtNonOption=true; token
gobbling/bursting.
TARGETS: processOptionToken, burstToken, gobble, processSingleHyphen handling of "--" mid-args.
ORACLES: PosixParserTest::testStop2 assertions on hasOption and getArgList; CLI-164 semantics.
ORACLES: CommandLine/Options state after parse must match opt/arg separation from test args.
CASES: {"-a","-b","foo","--","-x","-y"} with stopAtNonOption=true (options before, args after).
CASES: "--" after a non-option token; ensure trailing token and hyphen tokens stay in args.
CASES: boundary grouped options "-ab", lone "-", empty tail, option then "--" immediately.
RISKS: No spec/javadoc; expected behavior inferred only from failing test, not full CLI contract.
RISKS: Do not assume handling of unrecognized options or lone "-" beyond supplied evidence.