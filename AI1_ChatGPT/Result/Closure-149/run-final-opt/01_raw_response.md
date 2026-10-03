TARGETS: CommandLineRunner charset flag expansion into CompilerOptions input/output charset fields.  
TARGETS: AbstractCommandLineRunner option setup/run path; CommandLineRunner createOptions/createExterns.  
ORACLES: Existing trigger expects charset expansion result US-ASCII, not null.  
CASES: Valid --charset US-ASCII propagates to created CompilerOptions.  
CASES: Default/no charset behavior and charset use with JS input/output, if exposed by existing tests.  
RISKS: APIs/flag names and exact affected CompilerOptions fields are truncated; derive assertions from visible tests.