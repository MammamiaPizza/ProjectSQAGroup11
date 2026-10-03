TARGETS: DOMAttributeIterator and JDOMAttributeIterator attribute selection/positioning for QName names.  
ORACLES: Existing DOMModelTest::testNamespaceMapping expects rate:discount value "10%".  
ORACLES: Existing JDOMModelTest::testNamespaceMapping must resolve rate:discount without JXPathNotFoundException.  
CASES: Namespaced attribute lookup where same local name may exist under different namespace bindings.  
CASES: Verify selected attribute is rate:discount, not an incorrect attribute such as a "20%" value.  
CASES: Exercise iterator setPosition/getPosition and getNodePointer for matching namespaced attributes.  
RISKS: JDOM private matching behavior is not exposed by supplied signatures; test through JXPath evaluation.  
RISKS: No source fixture or QName construction details provided; rely on existing namespace-mapping test context.