TARGETS: parseTypeString, parseAndRecordTypeNode, parseInlineTypeDoc, Node position getters
ORACLES: node start/end (lineno/charno) match source offsets; testTextExtents checks parsed extents
CASES: union with >2 alternates; nested fn types with '=>'; whitespace before/after type; trailing
comma; @param/@return with type
RISKS: bug in private helpers unknown; may be extractBlockComment/trimEnd altering position; no
exact fix reference