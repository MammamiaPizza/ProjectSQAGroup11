TARGETS: FastDateParser.init() — strategy selection for quoted zone/era text patterns; parse
methods.
ORACLES: parse(null) returns null for patterns with unsupported locale-sensitive strategies (e.g.
'z', era text).
CASES: Pattern with quoted text containing 'z'/'Z' or era-symbol after 'G' in unsupported locale;
locale=ja_JP_JP vs default; empty/null source; normal date strings.
RISKS: Locale-sensitive timezone/era strategies may fail silently; unquoted vs quoted token handling
ambiguous; no API for locale-dependent strategy validation.