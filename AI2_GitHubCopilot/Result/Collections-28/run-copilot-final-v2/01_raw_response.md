TARGETS: AbstractPatriciaTrie.clear(), put(), size(), containsKey(), prefixMap(),
getPrefixMapByBits(); use concrete PatriciaTrie<String>.
TARGETS: PrefixRangeMap (RangeMap) clear()/iterator; after clear the trie keeps non-prefix keys and
size shrinks correctly.
ORACLES: SortedMap/TreeMap semantics for range views: prefixMap(key).clear() removes only
matching-prefix entries and never throws (esp. NPE).
ORACLES: Compare post-clear keys/values/size against a manually expected set; assert no
NullPointerException from the reported regression.
CASES: Empty trie prefixMap().clear(); prefix with no matching keys (no-op, no NPE).
CASES: Single exact matching key; several keys sharing a prefix; clear whole map via prefixMap("").
CASES: Boundary/adjacent keys (common branch prefixes, bitIndex=-1/0 edges) so removal re-links
internal/external entries.
CASES: After prefixMap.clear(): its iterator/keySet/entrySet empty and containsKey false for removed
keys, true for kept keys.
RISKS: API truncated; inner TrieEntry/RangeMap are private, so assert via public PatriciaTrie and
Map contract.
RISKS: Avoid null keys (castKey/KeyAnalyzer likely reject); limit to String keys with the default
key analyzer.