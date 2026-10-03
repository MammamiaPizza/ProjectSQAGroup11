TARGETS: W3CDom.fromJsoup and convert produce namespace-aware W3C DOM elements.  
TARGETS: W3CBuilder.head, copyAttributes, and updateNamespaces manage namespace scope.  
ORACLES: namespacePreservation expects `http://www.w3.org/1999/xhtml`, not `http://example.com/clip`.  
CASES: Convert documents with namespace declarations and assert resulting element namespaceURI values.  
CASES: Cover nested namespace changes and restoration of an enclosing namespace after nested scope.  
RISKS: Trigger input structure and full intended namespace rules are not provided.