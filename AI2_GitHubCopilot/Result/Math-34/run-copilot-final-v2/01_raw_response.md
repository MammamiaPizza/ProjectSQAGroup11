TARGETS: iterator() returns an unmodifiable Iterator<Chromosome>.
ORACLES: Calling remove() on that iterator must throw UnsupportedOperationException (per MATH-779
test).
CASES: After next(), call remove() on a non-empty population → expect UnsupportedOperationException.
CASES: On empty population, calling iterator().remove() before any next() should also throw
UnsupportedOperationException.
CASES: Verify that after failed remove(), the underlying chromosome list remains unchanged.
RISKS: Only remove() is asserted; set() or add() via iterator might still allow mutation if not also
forbidden.