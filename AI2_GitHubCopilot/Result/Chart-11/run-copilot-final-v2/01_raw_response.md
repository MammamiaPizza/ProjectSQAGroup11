TARGETS: equal(GeneralPath,GeneralPath)
ORACLES: equality defined by identical segment types, coordinates, winding rule (same path iteration
sequence)
CASES: identical paths; same shape but different winding rule; different subpath counts; empty vs
non-empty
CASES: same coords but different transform; mixed curve/line segments; reversed order; zero-length
paths
CASES: boundary: null arguments; paths with only MOVE_TO; very large or exact floating-point coords
RISKS: bug likely in segment comparison (e.g., float equality, ignoring winding rule, wrong
iteration end)
RISKS: without the failing test code, exact failure unknown; must cover all GeneralPath equality
pitfalls
RISKS: limited to public API; cannot inspect internal path representation or platform-specific
rendering