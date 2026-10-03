TARGETS: PosixParser.flatten/options parsing path, especially processOptionToken and burstToken for unknown options.
ORACLES: Existing trigger expects UnrecognizedOptionException for testUnrecognizedOption2 input.
CASES: Unknown single option; unknown option within a burst token; stopAtNonOption true/false where applicable.
CASES: Valid recognized option and normal burst-token parsing to guard against over-rejection.
RISKS: Private helpers cannot be directly tested; exercise through the parser's public parsing API.
RISKS: Exact arguments/options for testUnrecognizedOption2 are not provided; derive only from available test context.