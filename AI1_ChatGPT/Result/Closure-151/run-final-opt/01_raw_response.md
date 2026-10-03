TARGETS: CommandLineRunner argument parsing and shouldRunCompiler() behavior for version-related invocation.
ORACLES: Existing CommandLineRunnerTest::testVersionFlag assertion is the only stated expected-result source.
CASES: Invoke protected runner path with the version flag; verify trigger-observed output/exit behavior.
CASES: Compare version flag alone against normal compiler-running argument handling where accessible.
RISKS: Flag declaration and output destination are not shown; do not assume version text, stream, or exit semantics.
RISKS: No bug-report details or non-buggy version behavior is available in this context.