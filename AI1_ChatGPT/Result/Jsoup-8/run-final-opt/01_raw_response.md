TARGETS: Node.toString(), outerHtml(), ownerDocument() for parentless nodes
ORACLES: Node API: parent() may be null; outerHtml()/toString() return HTML
CASES: Parentless Element.toString() completes without NullPointerException
CASES: Parentless Element.outerHtml() completes and matches its HTML serialization
CASES: Attached node serialization retains document output-settings behavior
RISKS: Context lacks triggering test assertions and exact expected serialized HTML