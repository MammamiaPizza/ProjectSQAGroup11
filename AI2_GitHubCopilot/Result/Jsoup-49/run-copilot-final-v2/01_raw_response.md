TARGETS: Node.wrap, Node.addChildren(index,Node...), reparentChild, removeChild
ORACLES: outerHtml output; child ordering/missing children; parent==null after removal
CASES: moving children among same parent (append); wrap+unwrap preserving text order
RISKS: no spec on siblingIndex updates; incomplete API list for reparent/reindex internals