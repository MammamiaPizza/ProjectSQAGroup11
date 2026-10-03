TARGETS: shouldRunCompiler() returns false for --version
TARGETS: initConfigFromFlags() parses --version and sets a flag
ORACLES: stdout contains version string; stderr empty; exit code 0
ORACLES: No compiler creation or file I/O when version requested
CASES: --version alone prints version, exits, no errors
CASES: --version with other flags still prints version, ignores them
CASES: --version after other unrecognized flags? Error first?
CASES: --version combined with --help (verify priority)
RISKS: Exact version string unknown; must match via pattern
RISKS: Cannot observe internal state; rely solely on output/exit