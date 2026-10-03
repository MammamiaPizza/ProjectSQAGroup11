TARGETS: AbstractCommandLineRunner.setRunOptions(CompilerOptions) &
CommandLineRunner.createOptions() charset propagation.
ORACLES: After --charset US-ASCII, CompilerOptions.getOutputCharset() (or equivalent) must return
"US-ASCII".
CASES: --charset US-ASCII → "US-ASCII"; no flag → null/default; --charset UTF-8; --charset
ISO-8859-1.
RISKS: CompilerOptions charset accessor hidden; may reside in OutputCharsetEncoder or nested config,
not a direct getter.