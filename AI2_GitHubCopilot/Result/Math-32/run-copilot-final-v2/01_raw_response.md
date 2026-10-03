TARGETS: PolygonsSet.computeGeometricalProperties, getVertices,
SegmentsBuilder.visitLeafNode/visitInternalNode
ORACLES: area matches expected for known polygon (e.g., unit square), getVertices returns correct
loops
CASES: empty tree → area=0; single convex polygon; disjoint polygons; open/infinite boundary
segments; tree with null attribute
RISKS: incomplete buggy source; exact cast in BSP traversal unknown; cannot verify attribute type
for leaf/internal nodes