TARGETS: JsonPointer.compile/valueOf/fromSegment and segment matching/index parsing behavior  
ORACLES: Trigger TestJsonPointer::testIZeroIndex expects getMatchingIndex() == -1 for the tested segment  
CASES: Compile pointer containing segment "0"; check getMatchingIndex, mayMatchElement, and property matching  
CASES: Normal numeric segments, nonnumeric segments, empty pointer, and multi-segment tail traversal  
CASES: Boundary index strings: leading zero, zero, large/overflow-like numeric text, and negative text  
CASES: Invalid pointer syntax and escape handling should be checked only where existing API exceptions define behavior  
RISKS: Exact input and broader expected semantics are unavailable beyond the reported "0" index assertion  
