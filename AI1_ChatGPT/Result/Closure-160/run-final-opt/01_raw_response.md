TARGETS: Compiler initOptions, getErrorLevel, report, getWarnings/getErrors during command-line quiet/checkSymbols flow.
ORACLES: Trigger expects exactly one warning or error; use compiler diagnostic counts/messages as result source.
CASES: Quiet mode with checkSymbols override enabled; verify the override still produces one diagnostic.
CASES: Compare normal quiet configuration versus explicit checkSymbols override at warning/error level.
CASES: Boundary: no symbol-check override under quiet mode, assert only documented diagnostic outcome.
RISKS: CommandLineRunnerTest setup/options are not provided; exact source, flags, and diagnostic identity are unknown.
RISKS: Compiler methods require initialization/options and likely integration through CommandLineRunner, not isolated calls.