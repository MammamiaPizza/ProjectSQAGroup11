TARGETS: Util.stripLeadingAndTrailingQuotes(String); preserve unmatched embedded quote content.
ORACLES: Trigger expects input ending foo "bar" to produce foo "bar", not remove final unmatched quote.
CASES: Normal: fully quoted string removes one leading and one trailing quote.
CASES: Boundary: only leading quote, only trailing quote, empty string, single quote.
CASES: Embedded quotes: quoted prefix/suffix only versus unmatched internal/terminal quote.
RISKS: Util is package-private; tests must be in org.apache.commons.cli.
RISKS: No source/spec beyond trigger; avoid assuming null handling or hyphen-method behavior.