TARGETS: HelpFormatter: printHelp, printUsage, and internal method appending option arg name
(setArgName)
ORACLES: testPrintOptionWithEmptyArgNameUsage asserts output equals "usage: app -f[]" (no arg text)
CASES: normal: option with arg name shows " -f <file>"; boundary: arg name "" shows " -f"; null arg
name?; long opt empty arg
RISKS: parser uses sample Option only; untested: wrapping, multiple options, newline, default arg
name overrides