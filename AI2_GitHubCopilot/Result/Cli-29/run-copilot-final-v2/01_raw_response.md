TARGETS: Util.stripLeadingAndTrailingQuotes; bug incorrectly strips trailing quote when input has
embedded quotes.
ORACLES: Only str starting and ending with " should have those outer quotes removed; otherwise
return str unchanged (null-safe).
CASES: "foo" → foo; ""foo "bar""" → expected "foo "bar"" (bug gives "foo "bar"); only leading quote;
only trailing quote; both with no embedded quotes; empty; single quote only; null.
RISKS: Actual implementation may use indexOf/lastIndexOf instead of startsWith/endsWith; behavior
for single-char str unknown; no specs beyond API.