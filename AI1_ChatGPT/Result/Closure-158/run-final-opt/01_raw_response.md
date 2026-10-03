TARGETS: CommandLineRunner warning-guard configuration/order; AbstractCommandLineRunner option setup.
TARGETS: DiagnosticGroups.forName and registered warning-group lookup used by --jscomp_* flags.
ORACLES: Existing triggers require exactly one warning/error for warning-guard ordering scenarios.
ORACLES: DiagnosticGroups.forName rejects an unregistered group via its Preconditions check.
CASES: Apply jscomp error/warning/off directives to the same diagnostic group in differing orders.
CASES: Verify final diagnostic level emits/suppresses the expected single diagnostic.
CASES: Valid registered group lookup and invalid group-name failure.
RISKS: Flag parsing/config APIs and exact group names are truncated; derive inputs only from existing tests.