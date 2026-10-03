TARGETS: PosixParser.flatten(Options,String[],boolean), processOptionToken, burstToken behavior.
ORACLES: Existing PosixParserTest::testStop3 assertion: expected 3 extra arguments, not 7.
CASES: stopAtNonOption=true with a non-option followed by option-like tokens; retain remaining inputs as args.
CASES: Normal option parsing before first non-option; verify recognized options are still tokenized.
CASES: Boundary: first argument non-option; subsequent tokens must not be burst or processed as options.
RISKS: Private helpers require indirect testing through parse/flatten-accessible test mechanisms.
RISKS: Context lacks exact test inputs, Options setup, and expected token/argument contents beyond count 3.