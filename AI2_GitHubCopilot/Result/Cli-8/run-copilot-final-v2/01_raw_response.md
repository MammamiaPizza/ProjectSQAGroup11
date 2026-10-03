TARGETS: printWrapped(PrintWriter, int, String) — wrapping of padded text.
TARGETS: findWrapPos(String, int, int) — wrap-point computation for hyphen/space.
ORACLES: Expected output from testPrintWrapped: word "DATE" must not be split; the wrap should occur
before it.
ORACLES: rtrim(String) used internally — verify trailing-space trimming after wrapping.
CASES: Normal: text shorter than width; boundary: text exactly at width; text with a word at the
wrap edge.
CASES: Boundary: text containing a hyphenated token at the wrap position; text with multiple spaces.
CASES: Error: null/empty text; width=0; width=1; text with very long token exceeding width.
RISKS: Only public API visible; internal renderWrappedText calls missing — cannot test intermediate
pads.
RISKS: Single failing test case may not cover all wrap positions; need additional assertions on line
breaks.