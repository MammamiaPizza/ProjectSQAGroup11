TARGETS: DefaultParser.handleConcatenatedOptions, handleToken, handleShortAndLongOption
ORACLES: BugCLI265Test; expected: -ab with a,b defined => both recognised, no leftover token/value
CASES: Normal: -ab with -a,-b options; boundary: -a with required arg; error: unrecognised -x in -xy
CASES: Mixed: -ab --long; empty token; -ab after stopAtNonOption; -a b (space-separated value)
RISKS: Mis-handling of unrecognised short option as value; required-arg consumption in concat
RISKS: currentToken/currentOption state corruption; interaction with isJavaProperty and skipParsing