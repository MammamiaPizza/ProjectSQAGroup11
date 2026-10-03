TARGETS: Compiler.getWarnings(), Compiler.getMessages(), Compiler.report(), ErrorManager.
processing, quiet-mode error suppression logic.
ORACLES: testCheckSymbolsOverrideForQuiet asserts exactly 1 warning/error; actual count diff
revealed bug.
ORACLES: getErrorCount() and getWarningCount() must sum to 1 for --check_symbols with --quiet.
CASES: normal: quiet mode, no other flags, expect 0 warnings/errors.
CASES: boundary: quiet + --check_symbols override produces exactly 1 warning/error (the bug
trigger).
CASES: error: quiet + --check_symbols + other warnings should still count override warning
correctly.
CASES: multiple errors: --check_symbols without quiet yields multiple messages; quiet should not
suppress them incorrectly.
RISKS: CommandLineRunner and flag-parsing code not shown; exact flag behavior of --check_symbols
unknown.
RISKS: ErrorManager set/overridden internally; test may rely on custom error manager.
RISKS: Only Compiler class available; missing dependencies may limit reproducibility.