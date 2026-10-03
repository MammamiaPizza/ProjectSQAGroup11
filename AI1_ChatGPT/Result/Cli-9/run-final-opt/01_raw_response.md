TARGETS: Parser.checkRequiredOptions() missing-required exception message formatting.
ORACLES: Trigger assertions require missing option keys separated by ", " (e.g., "f, x", "b, c").
CASES: Parse options with two required options omitted; assert MissingOptionException message includes comma+space.
CASES: Preserve single missing required option behavior; no separator issue.
CASES: Required options satisfied should not throw missing-required exception.
RISKS: Parser is abstract; use an existing concrete parser/API in this version.
RISKS: Context only identifies message spacing; do not assume ordering beyond existing required-option order.