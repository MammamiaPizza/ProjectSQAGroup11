TARGETS: FilteringParserDelegate.nextToken(), getMatchCount()
ORACLES: Asserted match counts 1,2,3 in BasicParserFilteringTest failures
CASES: Single match, allow multiple, multiple match, index match; with/without path
CASES: _allowMultipleMatches true/false; _includePath flag states
RISKS: All triggers give count 0; bug likely in path-aware filter-matching logic