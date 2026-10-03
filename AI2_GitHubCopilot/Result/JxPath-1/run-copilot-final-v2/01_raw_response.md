TARGETS: DOMNodePointer.testNode(NodeTest), JDOMNodePointer.testNode(NodeTest) — fix NPE when
namespace is null
TARGETS: getNamespaceURI(), namespaceIterator(), attributeIterator() — likely return null namespace
contexts
ORACLES: testGetNode in DOMModelTest and JDOMModelTest should pass without NullPointerException
after fix
ORACLES: testNode must return correct boolean for given NodeTest and node properties
CASES: node with defined namespace, node without namespace, node with default namespace
CASES: NodeTest with wildcard, specific prefix, and null namespace mapping
CASES: boundary — null NodeTest argument, null node, empty namespace URI string
RISKS: Full test source and stack trace not provided; assumptions based on NPE trigger description
only
RISKS: Behavior of testNode may depend on underlying DOM/JDOM model; edge-case coverage limited