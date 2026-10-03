TARGETS: CommandLineRunner.initConfigFromFlags parsing --jscomp_error/--jscomp_warning/--jscomp_off
ordering
TARGETS: AbstractCommandLineRunner.setRunOptions applying warning-level changes to CompilerOptions
TARGETS: DiagnosticGroups.forName resolving group names to DiagnosticGroups (e.g., checkTypes,
suspiciousCode)

ORACLES: CommandLineRunnerTest expected error/warning counts after guard-ordered runs; joined
diagnostic groups in DiagnosticGroups registry

CASES: Guards applied in sequence: error→off=suppressed; off→error=promoted; warn→error→off
chaining; empty/null guard lists; unknown group name

RISKS: Truncated signatures hide guard-list fields (jscompError/jscompWarning/jscompOff) and
setRunOptions internals; actual ordering bug in initConfigFromFlags or setRunOptions missing