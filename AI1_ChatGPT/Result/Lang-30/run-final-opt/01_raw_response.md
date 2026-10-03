TARGETS: StringUtils.containsAny, containsNone, indexOfAny, indexOfAnyBut overloads using char[]/String.  
ORACLES: Trigger-test assertions provide expected booleans and indices for supplementary-character inputs.  
CASES: Valid surrogate pair in source/search chars; verify matching treats the pair consistently.  
CASES: Isolated/malformed supplementary surrogate in source or search chars; verify no false match.  
CASES: indexOfAny with supplementary chars expects index 2 rather than matching a leading surrogate at 0.  
CASES: indexOfAnyBut with supplementary chars expects index 2 rather than 3.  
CASES: containsNone supplementary and malformed-surrogate cases expect true; containsAny malformed cases expect false.  
RISKS: Java char indexing and surrogate-pair boundaries can cause partial-code-unit matches.  
RISKS: Context omits full test inputs and overload implementations; derive literals only from existing buggy-version tests.