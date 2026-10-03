TARGETS: CommandLineRunner.initConfigFromFlags(String[],PrintStream) must parse a --version flag.
TARGETS: shouldRunCompiler() must return false when version is requested, skipping compilation.
TARGETS: main(String[]) must print the version line to the configured output/error stream and exit.
ORACLES: CommandLineRunnerTest::testVersionFlag asserts version output and no compile; exact text
from test source.
ORACLES: Version string likely from the compiler/version constant or resource bundled with
Closure-151b.
CASES: NORMAL: --version alone prints one version line and exits cleanly without compiling.
CASES: NORMAL: --version with other valid args still prints version and skips compilation.
CASES: BOUNDARY: single-dash -version vs --version; verify both parse or only double-dash.
CASES: ERROR: absent flag must not print version; unknown flags should still fail before version
handling.
RISKS: Flags snippet shows no --version field, so it may be missing/undefined in the buggy version.