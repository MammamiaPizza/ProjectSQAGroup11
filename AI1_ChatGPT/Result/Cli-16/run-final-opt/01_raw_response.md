TARGETS: WriteableCommandLineImpl.addOption/getOptions preserve insertion order and retain all added options.
TARGETS: GroupImpl.validate enforces required child/parent options and group maximum occurrences.
TARGETS: GroupImpl/OptionImpl.findOption and processing resolve child triggers without losing parent semantics.
ORACLES: Existing CLI-123 trigger tests define missing-parent and parent maximum validation outcomes.
ORACLES: Existing *CommandLineTest.testGetOptions_Order defines expected option list identity/order.
CASES: Add help, login, and anonymous target options; assert getOptions returns all in insertion order.
CASES: Parse one or multiple child options without parent; verify required-parent validation behavior.
CASES: Parse parent plus child beyond parent maximum; expect maximum restriction failure.
RISKS: Context omits constructors/builders, exact exception messages, and full parser setup.