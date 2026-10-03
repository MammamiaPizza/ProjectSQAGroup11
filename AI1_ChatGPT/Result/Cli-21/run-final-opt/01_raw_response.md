TARGETS: GroupImpl parsing/validation of option arguments; WriteableCommandLineImpl.looksLikeOption(String).
TARGETS: WriteableCommandLine methods storing values/defaults and option-trigger lookup behavior.
ORACLES: Existing BugCLI150Test expects --num to accept -42, not throw Unexpected -42.
ORACLES: Option/group definitions and parser validation are the only supplied expected-behavior sources.
CASES: Numeric-valued --num followed by -42; assert parsed value is retained and no OptionException occurs.
CASES: Option-like prefixes/triggers versus negative numeric tokens in looksLikeOption and group matching.
CASES: Boundary signed numeric strings (for example -0) only if accepted by the configured argument type.
RISKS: Truncated GroupImpl context omits full parse API and argument-type rules; avoid assumptions beyond trigger test.