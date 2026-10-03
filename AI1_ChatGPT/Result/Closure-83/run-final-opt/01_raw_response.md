TARGETS: CommandLineRunner flag parsing and --version handling; shouldRunCompiler behavior after version request.  
ORACLES: Existing CommandLineRunnerTest::testVersionFlag2 assertion and runner stdout/stderr behavior.  
CASES: --version alone; verify version request does not proceed to compiler execution.  
CASES: --version combined with other arguments, matching testVersionFlag2 argument ordering.  
RISKS: Constructors are protected and Flags is private; tests may need package access/subclassing.  
RISKS: Context omits the --version field annotation, output text, and exact test assertion.