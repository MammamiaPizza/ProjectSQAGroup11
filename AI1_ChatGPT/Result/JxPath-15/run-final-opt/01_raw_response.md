TARGETS: UnionContext constructor, getDocumentOrder(), setPosition(int) union traversal/order behavior.
ORACLES: Existing DOMModelTest/JDOMModelTest testUnion expected XPath union result "John".
CASES: Union of /vendor[1]/contact[4] and /vendor[1]/contact[1] returns document-first contact.
CASES: Verify setPosition selects union members by document order, not input-expression/context order.
CASES: Boundary positions: first, last, and invalid/out-of-range positions if observable via setPosition result.
RISKS: Context lacks implementation details and broader UnionContext ordering/duplicate semantics.