TARGETS: FilteringParserDelegate.nextToken(), _nextToken2(), buffering; match limit via _allowMultipleMatches/_matchCount.  
ORACLES: Trigger expects filtered output "3", not "3 4", when multiple matches are disallowed.  
CASES: Filter input with first matching scalar 3 followed by another matching scalar 4; iterate nextToken to EOF.  
CASES: Assert only first match is exposed and getMatchCount reflects permitted matching behavior.  
CASES: Exercise includePath true/false around a matched nested value and verify token sequence/context.  
CASES: Boundary: no match, one match, adjacent matches, and matches in arrays versus objects.  
CASES: Check nextValue(), skipChildren(), clearCurrentToken(), and current-token state after filtering stops.  
RISKS: Constructor/filter setup and exact TokenFilter semantics are not provided; derive usage from existing tests only.