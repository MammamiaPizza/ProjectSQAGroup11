TARGETS: handleShortAndLongOption, handleConcatenatedOptions, handleToken parsing for short option
without value
ORACLES: Expected short option without arg must not consume following tokens as value (from bug
report)
CASES: Parse "-a -b" where both are short no-arg options; parse "-last" where "l" has no arg;
boundary: single short option, empty after
CASES: Concatenated "-abc" with no-arg options; error case: unknown short option; short option
followed by "--" separator
RISKS: Only given signatures; cannot inspect DefaultParser internals; expected behavior inferred
solely from trigger test description