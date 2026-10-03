TARGETS: NamespaceResolver namespace registration/lookup and DOMNodePointer namespace resolution during attribute creation.
ORACLES: Trigger test ExternalXMLNamespaceTest::testCreateAndSetAttributeDOM; no "Unknown namespace prefix: A".
CASES: Create/set a DOM attribute using prefix A when its namespace is declared externally/in scope.
CASES: Verify prefix A resolves to its namespace URI through DOMNodePointer/NamespaceResolver context.
CASES: Normal registered-prefix lookup; boundary default/empty prefix lookup if exercised by existing APIs.
RISKS: Context lacks source and exact expected DOM attribute name/URI; derive assertions only from trigger behavior.