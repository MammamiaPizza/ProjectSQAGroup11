TARGETS: ClassUtils.toClass(Object[])
ORACLES: compare against expected Class<?>[] from explicit arrays; JUnit assertions
CASES: null input; empty array; all-null array; mixed null/Class/primitive-wrapper elements
CASES: array of Class objects, primitives (int.class), String.class, Integer.class
RISKS: only buggy method specified; do not test unrelated methods lacking context
RISKS: expected exact primitive classes mapping in toClass output unverified