TARGETS: _nextToken2(), nextToken(), _matchCount
ORACLES: getMatchCount(), getCurrentToken(), trigger test expected value (no trailing NUMBER_INT
after match)
CASES: single match in array, multiple matches allowed vs disallowed,
_allowMultipleMatches=true/false
RISKS: _matchCount field, not extension point; must check count reset on failed second match; only
one Junit-3 test triggers