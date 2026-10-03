TARGETS: ArrayRealVector.add/subtract (all overloads), OpenMapRealVector.add/subtract (incl.
sparse-specific), constructors that combine vectors (e.g., ArrayRealVector(ArrayRealVector,
ArrayRealVector)), dotProduct.
ORACLES: Element-wise sum/difference; compute manually from input arrays; also use L1 norm, dot
product of simple vectors as test oracles.
CASES: Normal: short vectors ([1,2,3]), zeros, ones, negative values; Boundary: empty vector,
1-element, identical operands, sparse with many unset entries; Error: dimension mismatch, null
input.
RISKS: Different overloads may have independent bugs (e.g., double[] vs RealVector arg);
OpenMapEntry iterator may skip default entries, causing missing contributions; check sign and
magnitude of results.