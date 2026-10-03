TARGETS: PatternBuilder.withPattern; create()/reset() aggregation of options
ORACLES: Bug27575Test::testRequiredOptions expects required options=[[-h]], not [-c <arg>]
ORACLES: CLI-121 pattern semantics define which option char is marked required
CASES: normal: basic option chars, required marker, option with argument
CASES: required flag interplay with arg option (-c <arg>) to avoid misattribution
CASES: boundary: empty pattern, reset() then reuse, duplicate options, unknown char
CASES: error: invalid pattern chars failing validation via validator(char)
RISKS: exact pattern-string grammar/required-marker syntax not given in summary
RISKS: validator side effects and option display format partially inferred