TARGETS: AttributeContext.nextNode(), setPosition(int), reset(), getCurrentNodePointer()
ORACLES: Trigger XPath attribute::node() returns [10%, 20%] for DOM and JDOM models
CASES: Iterate vendor/product/price:amount attributes and verify both values are returned
CASES: Verify iteration position/reset behavior around attribute-node traversal
RISKS: Only failing XPath/result evidence is provided; no implementation details or broader semantics given