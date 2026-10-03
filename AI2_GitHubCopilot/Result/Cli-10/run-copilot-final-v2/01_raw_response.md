TARGETS: Parser.parse(), checkRequiredOptions(), setOptions()/getRequiredOptions() reset state for
reused Options.
ORACLES: MissingOptionException expected when reusing same Options across parse calls with missing
required args.
CASES: 1) parse once with required present, second parse missing required -> MissingOptionException;
2) parse once missing required -> throws; 3) reuse with all present -> no throw.
RISKS: Shared Options across threads; limited to parse-reuse scenario; test must ensure fresh parser
state per parse.