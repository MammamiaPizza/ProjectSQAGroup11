TARGETS: OptionGroup.setSelected(Option), getSelected(), addOption(Option), getNames(), getOptions()
ORACLES: Trigger tests expect selected option "bar" after parsing a long option in an OptionGroup.
CASES: Add long-option members; select "bar"; assert getSelected returns its option key/value used by parser.
CASES: Repeat selection of same option; select a different member and verify AlreadySelectedException behavior.
CASES: Boundary: setSelected(null) clears/changes selection only if supported by observed API behavior.
RISKS: Parser integration is relevant across BasicParser, GnuParser, and PosixParser.
RISKS: No source/body or Option constructor details supplied; avoid assuming option naming and null semantics.