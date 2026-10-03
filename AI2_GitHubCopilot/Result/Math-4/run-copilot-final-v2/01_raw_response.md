TARGETS: SubLine.intersection() in both 3D and 2D; 2D also .side(), .split()
ORACLES: NullPointerException on null crossing; intersection returns null when lines do not
intersect; parallel lines → null crossing
CASES: Non-intersecting 3D sublines; parallel 2D sublines; includeEndPoints=true/false; coincident
sublines; adjacent segments
RISKS: No source access to intersection() body; cannot confirm null-handling logic; assume return
null from Line.intersection() on parallel/no-intersect