TARGETS: prefixMap(K).clear(), PrefixRangeMap fixup/entrySet behavior, and parent trie size/content afterward.
ORACLES: Trigger PatriciaTrieTest::testPrefixMapClear; no NPE and Map/SortedMap clear semantics.
CASES: Populate matching and nonmatching keys; clear prefixMap; verify only prefixed entries are removed.
CASES: Clear an empty prefixMap and a prefixMap whose matching range has boundary/single entry.
CASES: After clear, query parent get/containsKey/size and iterate prefixMap/entrySet.
RISKS: Abstract class context omits concrete key data and full prefix-bit semantics; use existing PatriciaTrie API/tests.