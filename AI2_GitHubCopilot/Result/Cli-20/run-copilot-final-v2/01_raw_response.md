TARGETS: PosixParser.flatten(Options,String[],boolean) handling stopAtNonOption=true
TARGETS: burstToken / processOptionToken when encountering unrecognized token with stop flag
ORACLES: CommandLine.getArgList().size() equals expected number of trailing non-option args
ORACLES: CommandLine.getArgs() content exactly matches the input tail after the stop point
CASES: stopAtNonOption=true: known opts, then unrecognized token, then more known opts; expect
trailing args include those known opts
CASES: stopAtNonOption=false with same input: all known opts parsed, unrecognized becomes single arg
CASES: Boundary: empty args, all non-options, all recognized options, token "-" with no matching
option
CASES: Combined short options (e.g., -xvf) with one unrecognized option and stopAtNonOption=true
RISKS: No access to PosixParser internals; must verify only via CommandLine parsed results and
getArgs
RISKS: JUnit 3.8.1 requires TestCase subclass; existing test patterns (e.g., testStop3) guide
Options setup style