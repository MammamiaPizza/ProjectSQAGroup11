TARGETS: DOMAttributeIterator.testAttr(Attr,QName) namespace-aware attribute matching.
TARGETS: JDOMAttributeIterator.setPosition(int) attribute iteration with namespace.
ORACLES: Compare returned attr value with expected from test fixture (rate:discount="10%" vs "20%").
CASES: QName with namespace URI matches only attrs whose namespace URI equals that URI.
CASES: QName without namespace URI matches only unqualified attrs; qualified QName excludes
unqualified.
CASES: Element with multiple same-local-name attrs in different namespaces, correct attr returned
per QName.
CASES: Element without requested attribute: iterator yields empty or null NodePointer.
RISKS: Cannot see test XML structure or QName construction; limited to given failure messages.
RISKS: JDOMAttributeIterator inner logic not visible; need integration test with real JDOM/namespace
binding.