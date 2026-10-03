TARGETS: MathUtils.equals(double[],double[]), MathUtils.equalsIncludingNaN(double[],double[])
ORACLES: Expected that NaN values at same index should be considered equal (both methods), matching
the scalar equals with maxUlps contract
ORACLES: Arrays with +0.0 and -0.0 likely considered equal; +Inf equals +Inf, -Inf equals -Inf, −Inf
≠ +Inf; different lengths ⇒ false
CASES: Two arrays containing NaN at same index → expect true; NaN in one but not the other → false;
empty arrays → true
CASES: Arrays with +0.0/-0.0 mix; normal doubles exact match; one element differs; arrays of
different length; null arguments (NPE?)
CASES: Compare equals vs equalsIncludingNaN: arrays with only NaN (both true); NaN with non‑NaN
(equals returns false, includingNaN false)
RISKS: No definitive specification; behavior for signed zeros, denormals, and infinities is
inferred; original test may have expected different NaN handling
RISKS: Cannot verify fix; equalsIncludingNaN may also be buggy; edge cases with
Double.longBitsToDouble special values unknown