TARGETS: getName() for element with xmlns:prefix → QName prefix:local
TARGETS: getNamespaceURI(prefix) returns correct URI for declared prefix on ancestor
TARGETS: namespacePointer(prefix) returns valid pointer to namespace node
TARGETS: testNode with NamespaceTest(prefix) returns true for matching element
ORACLES: Node.getNamespaceURI() & Node.getPrefix() per w3c DOM; JXPath test expects resolution of
/ElementA/B:ElementB
CASES: child element with prefix inherited from parent, no local xmlns:prefix
CASES: default namespace (xmlns="uri") and explicit prefix on same element
CASES: unknown prefix → null namespace, fallback to default namespace
CASES: multiple nested xmlnspre declarations overriding same prefix
RISKS: External namespace mapping may not propagate to child nodes; DOMNodePointer may cache wrong
namespace; tests must use fresh DOM