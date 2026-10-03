TARGETS: _parseIndex(String); ctor sets _matchingElementIndex; getMatchingIndex();
mayMatchElement(); compile/valueOf/fromSegment.
ORACLES: testIZeroIndex AssertionFailedError expected -1 but was 0; getMatchingIndex() should be -1
for offending token.
ORACLES: inferred offending token is a leading-zero numeric segment (e.g. "00") parsed to 0;
expected -1.
CASES: "00","01","007" -> _parseIndex -1, mayMatchElement() false.
CASES: "0" -> index 0, mayMatchElement() true (single-zero boundary).
CASES: "" empty, "abc", "-1", "1a" -> -1 (non-canonical/invalid).
CASES: "12","42" -> parsed int; overlong/int-overflow -> -1.
CASES: matchElement(0)/matchProperty("00")/tail() behavior on parsed index.
RISKS: exact trigger input not shown; leading-zero case inferred from bug summary/failure.
RISKS: limited to supplied signatures/summary; no other APIs or golden outputs than listed test.