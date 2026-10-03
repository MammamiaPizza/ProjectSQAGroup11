TARGETS: MultidimensionalCounter.getCount(int...c), getCounts(int index),
Iterator.getCount()/getCounts()/next()
ORACLES: For all valid counts c: getCounts(getCount(c)) == c; iterator getCount() monotonic
0..size-1; getCounts() matches getCounts(getCount())
CASES: 1D [5]; 2D [3,4] and [4,3]; 2D [2,2]; 3D [2,3,4]; boundary dims=1; iteration full-walk;
OutOfRangeException for bad counts/index
CASES: Known failure trigger: sizes {4,3} (or {3,4}) — check that iterator’s getCount() and
getCounts() stay consistent with reverse conversion
RISKS: Iteration order (row-major/column-major) not documented; bug isolated to getCount(int...) /
getCounts(int) mapping for some size combos
RISKS: Only testIterationConsistency fails; must infer expected behavior from property equalities;
no reference implementation available