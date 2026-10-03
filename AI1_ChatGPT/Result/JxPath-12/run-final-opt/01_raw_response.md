TARGETS: DOMNodePointer namespace/name matching used to resolve /ElementA/B:ElementB in DOM contexts.
ORACLES: ExternalXMLNamespaceTest::testElementDOM must resolve without JXPathNotFoundException.
CASES: Namespaced child B:ElementB under ElementA; verify XPath lookup yields an existing value/node.
CASES: Exercise DOM element name/namespace extraction and prefix-to-namespace resolution.
RISKS: Exact returned value, XML fixture, and intended namespace rules are not provided.
RISKS: Do not infer behavior from another version; only trigger failure identifies the expected outcome.