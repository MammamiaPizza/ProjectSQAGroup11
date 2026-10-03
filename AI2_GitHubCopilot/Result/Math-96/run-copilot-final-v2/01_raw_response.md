TARGETS: Complex.equals(Object), hashCode(), getReal(), getImaginary(), isNaN(), isInfinite().
TARGETS: Arithmetic methods likely unchanged; focus tests on equality/hash contract, not numeric
formulas.
ORACLES: Java equals/hashCode contract: equal objects must have equal hash; null/other types must
return false.
ORACLES: MATH-221 regression via ComplexTest.testMath221 expecting same-value Complex instances to
be equal.
CASES: Normal equal pairs: (1,2)==(1,2), (1,-2)==(1,-2), ZERO==new Complex(0,0), ONE, I.
CASES: Boundaries: NaN in real/imag or both; Complex.NaN; +Infinity/-Infinity; +0.0 vs -0.0
components.
CASES: Error/type: equals(null), equals(non-Complex), self-compare, equal values in different
instances.
RISKS: Trigger source/exact values not shown; avoid inventing expected equality beyond supplied
semantics.
RISKS: Complex.toString is identity-based in failures; do not assert on string form, use
equals/hashCode only.
RISKS: Double -0.0/+0.0 and NaN sign/canonical bits can vary by platform; prefer behavioral
assertions.