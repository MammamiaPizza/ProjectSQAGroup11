TARGETS: DefaultParser.parse overloads; protected handleConcatenatedOptions(String).
ORACLES: BugCLI265Test expects null but buggy parse returns "-ab".
CASES: Concatenated short options matching CLI-265 trigger input.
CASES: Normal separate short options; boundary concatenation with option arguments.
RISKS: Exact Options setup and intended assertions are not provided beyond trigger failure.
