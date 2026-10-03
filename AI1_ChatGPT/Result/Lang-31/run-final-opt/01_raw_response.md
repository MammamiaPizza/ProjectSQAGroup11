TARGETS: StringUtils.containsAny(CharSequence,char[]) and containsAny(CharSequence,CharSequence) supplementary chars.
ORACLES: Trigger assertions require false where a lone surrogate matches part of a supplementary search character.
CASES: Nonmatching lone high/low surrogate versus supplementary code-point char[]; expect false.
CASES: Same surrogate-boundary cases for CharSequence searchChars; expect false.
CASES: Complete matching supplementary character in input/search; verify true if exposed by existing tests.
RISKS: API overload details and exact trigger literals are unavailable due to truncated context.