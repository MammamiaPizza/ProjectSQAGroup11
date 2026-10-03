TARGETS: ArrayUtils.add(T[] array, int index, T element), especially runtime component-type preservation.
ORACLES: Trigger testJira567 and absence of ArrayStoreException; returned array contents/type.
CASES: Add compatible element into a typed reference array at start, middle, and end.
CASES: Add to null/empty typed arrays if supported; verify resulting runtime array type and element.
RISKS: Generic erasure may allocate from element type and fail when element is a subtype/supertype mismatch.
RISKS: API excerpt omits exact add overload semantics and null/index exception contracts.