TARGETS: Parser.parse(options,args,properties[,stopAtNonOption]) and protected processProperties(Properties).
ORACLES: Existing trigger ValueTest.testPropertyOptionFlags is the expected-result source.
CASES: Property-backed option flags with absent command-line arguments; verify resulting CommandLine option state.
CASES: Properties containing true/false-like values for configured flag options, per trigger behavior.
CASES: Command-line option versus property value precedence, if exposed by parse result.
RISKS: Parser is abstract; tests require an existing concrete Parser implementation.
RISKS: No option/value semantics beyond CLI-201 trigger are provided; avoid assuming property coercion rules.