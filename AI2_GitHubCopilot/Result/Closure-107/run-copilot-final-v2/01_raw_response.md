TARGETS: CommandLineRunner.createOptions() warning-guard wiring; WarningGuardSetter.addValue()
ORACLES: Compiler.getWarnings()/getErrors() count == 0 after compilation
CASES: --jscomp_warning, --jscomp_off, --jscomp_error with various warning names; empty/flags unset
CASES: --warning_level QUIET/VERBOSE; mixing guard flags; duplicate flags
CASES: Run on trivial JS input with no inherent warnings; expect zero warnings from compiler
RISKS: Need to instantiate runner non-interactively; cannot access protected inner methods directly