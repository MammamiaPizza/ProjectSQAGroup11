TARGETS:  WriteableCommandLineImpl.getNormalised() — default values missing from output
TARGETS:  WriteableCommandLineImpl.toString() — affects normalized-list formatting with defaults
ORACLES: Default values in defaultValues map should appear in getNormalised() list trailing after
explicit args
ORACLES: BugCLI158Test expected strings show default appended to getNormalised: "[1, 1000]" or "[1,
2, 10000]"
CASES:    Option with default value and fewer args than maximum → default appended
CASES:    Option with default and exactly maximum args → default not appended
CASES:    Option with no default and zero args → no default insertion
CASES:    Boundary: option has default but args already match/exceed maximum → default suppressed
RISKS:    WriteableCommandLineImpl only shown — other classes (Option, Argument) signatures absent
RISKS:    Exact trigger logic (when to inject default) unclear without WriteableCommandLine source
body