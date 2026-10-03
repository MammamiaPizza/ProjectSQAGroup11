TARGETS: TreeBuilderState.process for Character tokens handling '\0' (NUL), especially InBody,
InCell
ORACLES: Expected output from parser includes '\0' as text, not removed or replaced; test expects
containing text "0"
CASES: Input with '\0' in element text, in <td>, just '\0' alone, multiple null chars, null at
boundaries
RISKS: Bug may be state-specific; exact failing state unknown; only trigger test name available, no
full repro