TARGETS: ShapeUtilities.equal(GeneralPath, GeneralPath); segment-by-segment path equality.  
ORACLES: Trigger test testEqualGeneralPaths; GeneralPath iterator coordinates/types provide expected equality.  
CASES: Equal paths with matching moveTo/lineTo/closePath segments should return true.  
CASES: Differing segment coordinates, segment types, or segment counts should return false.  
CASES: Boundary paths with only moveTo, closePath, and multiple subpaths.  
RISKS: Context omits current implementation and exact null-handling contract.