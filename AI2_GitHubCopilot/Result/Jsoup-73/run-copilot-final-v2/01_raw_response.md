TARGETS: fromJsoup, convert, W3CBuilder.head, updateNamespaces, copyAttributes
ORACLES: Target Element getNamespaceURI must match source org.jsoup.nodes.Element.namespace()
CASES: single element with explicit namespace; nested elements with different namespaces; prefixed
attributes
RISKS: updateNamespaces may cache last-seen namespace, overriding correct one; test fixture
namespace parsing could differ