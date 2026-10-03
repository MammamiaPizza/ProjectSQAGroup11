TARGETS: DateTimeZone.forID(String), getConvertedId(String)
ORACLES: forID must return a zone whose getID() equals or canonically maps to the input; documented
tzdb IDs
CASES: known IDs (UTC, Europe/London, WET), aliased/deprecated IDs, null, empty, unknown IDs
RISKS: cannot see getConvertedId mapping logic or old test source; provider regression may affect
many ID lookups