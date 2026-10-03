TARGETS: contains(char) – bug: scans buffer.length instead of size, causing false positives for
chars beyond the valid string.
TARGETS: indexOf(char), indexOf(char,int), lastIndexOf(char), lastIndexOf(char,int) – likely share
same off-by-size loop.
ORACLES: Expected result = new String(buffer,0,size).contains(ch) or toString().contains(ch) for
consistent behaviour.
ORACLES: After setLength(n) or clear(), contains(ch) must return false even if the underlying buffer
still holds ch beyond size.
CASES: Normal – "abc".contains('b') must be true; "abc".contains('x') must be false (size=3).
CASES: Boundary – append("abc"); setLength(2); contains('c') must be false; char at index 2 is
beyond valid content.
CASES: Boundary – clear(); all contains calls must return false for any char, regardless of buffer
remnants.
CASES: Error – contains('\0') on size=0 but buffer may have zero char; must return false, not throw.
RISKS: Only contains(char) is triggered by the regression test;
indexOf/lastIndexOf/contains(StrMatcher) may remain broken.
RISKS: A narrow fix to contains(char) alone leaves other scanning methods with the same buffer
length-vs-size mistake.