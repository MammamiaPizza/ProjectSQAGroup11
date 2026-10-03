TARGETS: extractMultilineTextualBlock – extracts @license/@preserve text
TARGETS: extractSingleLineBlock – single-line comment text extraction
TARGETS: extractBlockComment – handles multi-line block extraction
ORACLES: testParseLicense/testParsePreserve expected outputs, no leading whitespace
CASES: @license with newline, space-only, mixed indentation, empty content
CASES: @preserve with ASCII/non-ASCII, leading/trailing line breaks
CASES: boundary: exactly one newline, whitespace-only text block
RISKS: No corrected code; rely on test failures to deduce expected behavior
RISKS: Private helpers (like trimEnd) may need internal logic scrutiny