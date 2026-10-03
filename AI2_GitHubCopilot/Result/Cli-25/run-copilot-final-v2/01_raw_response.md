TARGETS: renderOptions description wrapping via printWrapped with nextLineTabStop.
TARGETS: findWrapPos computing line-break positions for wrapped text.
ORACLES: Expected string from BugCLI162Test::testLongLineChunkingIndentIgnored.
ORACLES: Continuation lines indented by nextLineTabStop (default descPad+leftPad?).
CASES: Option description just exceeding default width; description 2–3× width; description exactly
at width; multiple long options; OptionGroup with long desc.
RISKS: Only one trigger test; exact expected formatting unknown without test code; edge cases with
embedded line breaks or leading spaces unclear.