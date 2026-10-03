TARGETS: Options.getMatchingOptions(String), getOption(String), hasLongOption(String), hasOption(String)
ORACLES: CLI-252 trigger: exact "prefix" must not be ambiguous when "prefixplusplus" also exists
CASES: Add long options "prefix" and "prefixplusplus"; query exact "prefix" matching/lookup
CASES: Query unique prefix of a long option; verify returned matching option behavior
CASES: Query shared non-exact prefix; expect ambiguity-related matching behavior per existing API use
CASES: Query absent name/prefix and short-vs-long option names
RISKS: Exact-match precedence may affect getMatchingOptions and callers such as getOption/hasOption
RISKS: Context lacks implementation and full expected semantics for empty/null inputs and ambiguity exception source