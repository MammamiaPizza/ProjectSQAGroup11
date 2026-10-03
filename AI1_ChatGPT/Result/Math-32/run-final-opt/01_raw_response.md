TARGETS: PolygonsSet.getVertices and SegmentsBuilder.visitInternalNode boundary-attribute handling.
ORACLES: MATH-780 trigger: PolyhedronsSetTest.testIssue780 must not throw ClassCastException.
ORACLES: getVertices output/region geometry only where established by existing public API/tests.
CASES: BSP tree internal nodes with BoundaryAttribute; invoke getVertices through affected 3D trigger path.
CASES: finite closed polygon loops; verify vertex-loop construction remains usable.
CASES: unbounded/open boundary intervals, producing null endpoints in segments.
RISKS: Constructor/tree setup details and expected vertex coordinates are not provided.
RISKS: Do not derive expected behavior from another program version.