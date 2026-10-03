TARGETS: Parser.processProperties(Properties) — must not propagate AlreadySelectedException for
group conflicts on property-set options.
TARGETS: DefaultParser.handleProperties(Properties) — null-safety for Option lookup when property
key is unrecognized.
ORACLES: testPropertyOptionGroup expects no AlreadySelectedException if properties supply a second
option from a mutually exclusive group.
ORACLES: testPropertyOptionUnexpected expects no NullPointerException; unknown properties should be
silently ignored.
CASES: Normal: single property sets a valid option; variant: property sets option already set by CLI
arg from same group.
CASES: Boundary: two properties set different options of same group; properties with empty
key/value; null Properties argument.
CASES: Error: property key not in Options, leading to null Option; multiple unrecognized properties.
RISKS: Desired handling for properties in option groups is undocumented; only test names reveal
expected outcomes.