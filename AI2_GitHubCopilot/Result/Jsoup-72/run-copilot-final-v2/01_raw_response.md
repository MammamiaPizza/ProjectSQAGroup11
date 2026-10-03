TARGETS: CharacterReader.consumeTo(char), consumeToAny(char...), consumeToAnySorted(char...)
ORACLES: Javadoc: consumeTo returns string up to delimiter or to end; no
StringIndexOutOfBoundsException at EOF.
ORACLES: Expected fix: when delimiter absent and reader at end, return empty string or remaining
content without exception.
CASES: consumeTo(char) with delimiter at last position, after last position, absent early, absent at
end.
CASES: consumeToAny(chars) delimiter present at end, absent with reader at end, absent before end,
empty input.
CASES: Boundary: input size near buffer limits (readAheadLimit, maxBufferLen); multiple advance
calls before consume.
CASES: Error: after consumeTo misses delimiter, subsequent read (advance/consume) must not throw on
stale index.
RISKS: Internal cacheString/rangeEquals off-by-one; only public API testable; buffer state
invisible.
RISKS: Other consume methods (consumeToAnySorted) may share the same bug; only trigger tests known,
moderate coverage risk.
RISKS: Dependence on exact Jsoup-72b environment; no access to fixed version for comparison; assume
provided API is complete.