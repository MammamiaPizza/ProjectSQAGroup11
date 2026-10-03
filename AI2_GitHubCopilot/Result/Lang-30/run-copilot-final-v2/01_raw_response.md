TARGETS: containsNone(CharSequence,char[]), containsAny(CharSequence,char[]),
indexOfAnyBut(CharSequence,char[]), indexOfAny(CharSequence,char[]), analogous String-based
overloads
ORACLES: expected booleans/int from test assertions; supplementary chars (>=U+10000) must be treated
as single code points, not split into surrogates
CASES: supplementary chars in search set found/not-found; supplementary in search string and
found/not-in-CS; bad (unpaired) surrogates as lone high/low surrogates
RISKS: truncated API listing—need full signatures for char[]/String overloads of the five methods;
internal code-point handling may incorrectly split surrogates in CharSequence.length-based loops