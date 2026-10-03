TARGETS: abbreviate(String str, int lower, int upper, String appendToEnd)
ORACLES: Javadoc contract; no exceptions on valid inputs; result length <= upper + append length
CASES: normal: str longer than upper, lower<upper, append non-null → proper truncation
CASES: boundary: lower=0, upper=str.length, lower==upper, upper<lower, append null/empty
CASES: null str → ""; null append → ""; lower>upper → ""; lower/upper negative → treat as 0?
CASES: edge: upper - lower < append.length causing index overlap (likely bug location)
CASES: bug repro: input "abc def ghi jkl mno...", lower=4, upper=15, append="..." →
StringIndexOutOfBoundsException:15
RISKS: only buggy source available; expected behavior inferred from Javadoc, not a correct
implementation
RISKS: other WordUtils methods may be indirectly affected; focus on abbreviate for regression
RISKS: existing test testAbbreviate triggers the bug—ensure new tests cover that and additional edge
cases