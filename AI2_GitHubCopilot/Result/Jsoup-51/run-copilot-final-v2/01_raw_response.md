TARGETS: CharacterReader.consumeToAny, consumeToAnySorted, rangeEquals, cacheString; bug likely in
multi-byte char consumption or equality
ORACLES: consumeToAny stops exactly after first occurrence of any delimiter; rangeEquals returns
correct substring equality for all char ranges
CASES: ASCII-only delimiters; non-ASCII (é, ñ, 中文) delimiters; supplementary characters (💯);
empty input; delimiter at pos 0/end; no match; mixed ASCII+non-ASCII
RISKS: Limited to buggy version signatures; private cacheString untestable directly; no external
spec for non-ASCII behavior; may depend on charset normalization