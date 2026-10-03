TARGETS: DOMNodePointer.getRelativePositionBy* methods; JDOMNodePointer.getRelativePositionBy*
methods
ORACLES: testAxisFollowing expects <//location[2]/following::node()[2]> ending with
vendor[1]/product[1][] (no extra child)
ORACLES: testAxisPreceding expects <//location[2]/preceding::node()[3]> ending with
location[1]/[employeeCount[1]/text()][1]
CASES: following axis must exclude attribute/namespace children from step count; verify self-closing
element path suffix
CASES: preceding axis ordering: text() nodes must appear before sibling elements; test [3], [2],
[1], [0], overflow index
RISKS: Limited to bug summary; actual node-set traversal logic (following/preceding iterators) not
visible