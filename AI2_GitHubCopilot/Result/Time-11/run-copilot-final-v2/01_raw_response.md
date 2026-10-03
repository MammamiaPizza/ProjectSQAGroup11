TARGETS: ZoneInfoCompiler.compile() – parses zone info files and builds DateTimeZone map.
TARGETS: DateTimeOfYear.addCutover() – adds DST cutover to builder.
TARGETS: Zone.buildDateTimeZone() – constructs DateTimeZone from parsed data.
ORACLES: Compare resulting DateTimeZone's offsets and transitions against expected values derived
from input data.
ORACLES: Use predictable output for known input (e.g., fixed UTC offset without DST).
CASES: Normal DST rule with positive and negative savings, varying years.
CASES: Boundary years – 0, negative, large; leap-day rule on Feb 29; month=-1.
CASES: Empty rule set; missing rule reference; malformed input line.
RISKS: No independent oracle – test relies on assumed correct behavior; input files not provided.