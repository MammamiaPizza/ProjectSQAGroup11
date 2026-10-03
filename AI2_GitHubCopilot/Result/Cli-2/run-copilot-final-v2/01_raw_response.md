TARGETS: PosixParser.parse(Options,String[]) handling of "-o" via burstToken, processOptionToken,
processSingleHyphen; stopAtNonOption flag.
ORACLES: POSIX: parse() must not throw for registered options; CommandLine.hasOption("o") true,
getOptionValue returns correct value.
ORACLES: Check CommandLine against Options definition: recognized options present, unrecognized via
proper exceptions.
CASES: Normal: parse "-o val" for defined option returns hasOption true; parse "--other" for long
options.
CASES: Combine: "-abc" where a,b are flags, c takes arg; burstToken should split correctly without
UnrecognizedOptionException.
CASES: Boundary: parse empty args, "-", "--", "-- -opt"; stopAtNonOption=true and false affect
recognition.
CASES: Error: unrecognized "-x" throws UnrecognizedOptionException; missing required arg for "-o"
throws MissingArgumentException.
CASES: Reproduction: define Option("o",true,"desc"), parse {"-o"} must not throw; current bug
triggers UnrecognizedOptionException.
RISKS: Private burstToken/processOptionToken not directly testable; must test via public parse().
RISKS: No fix version to confirm expected "stopAtNonOption=false" behavior; tests may rely on
exception message strings.