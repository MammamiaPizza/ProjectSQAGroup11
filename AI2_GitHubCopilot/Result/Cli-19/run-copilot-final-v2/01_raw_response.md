TARGETS: PosixParser.flatten, processOptionToken, burstToken — unrecognized-option detection during
token parsing
ORACLES: testUnrecognizedOption2 expects UnrecognizedOptionException to be thrown when an unknown
option is supplied
CASES: Unrecognized short (-z), long (--bad), after non-option values, stopAtNonOption true/false,
empty string
RISKS: Only one trigger test known; no source diff; bug could also affect MissingArgumentException
or stopAtNonOption logic