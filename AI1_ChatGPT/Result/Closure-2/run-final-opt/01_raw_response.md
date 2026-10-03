TARGETS: TypeCheck.process/processForTesting/check traversal of interface inheritance declarations.
ORACLES: Trigger test expects no NullPointerException for interfaces extending nonexistent interfaces.
ORACLES: Use compiler/type-check diagnostics emitted by the existing TypeCheckTest harness as expected source.
CASES: Interface extends one nonexistent interface; verify traversal completes and reports its normal diagnostic.
CASES: Interface extends multiple nonexistent interfaces; verify no crash and diagnostics remain stable.
CASES: Valid interface extending a declared interface; preserve normal inheritance checking.
CASES: Interface implementing another interface; retain documented "can only extend" diagnostic.
RISKS: Available context lacks diagnostic keys/messages and TypeCheckTest helper APIs; avoid asserting invented text.