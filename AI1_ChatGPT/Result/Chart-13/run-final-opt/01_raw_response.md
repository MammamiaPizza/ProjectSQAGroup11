TARGETS: BorderArrangement arrangement under width-constrained sizing; trigger reaches invalid Range bounds.
ORACLES: Existing trigger test is the expected-result source; sizing must not create Range lower > upper.
CASES: Width constraint with blocks whose required widths leave zero or negative remaining center width.
CASES: Normal width-constrained layout with sufficient width for assigned border blocks.
CASES: Boundary widths near total required left/right widths, including exact fit and zero remainder.
CASES: add(Block,Object) placement followed by clear(), then constrained sizing.
CASES: equals(Object) for same arrangement state, different state, null, and non-BorderArrangement object.
RISKS: arrangeNN is protected; direct test access may require package-level test support.
RISKS: No bug report or expected Size2D values are provided beyond the failing Range invariant.