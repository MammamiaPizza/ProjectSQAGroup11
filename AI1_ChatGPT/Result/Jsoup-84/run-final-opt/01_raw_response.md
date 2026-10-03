TARGETS: W3CDom.fromJsoup, convert, W3CBuilder.head, copyAttributes, updateNamespaces.  
ORACLES: Trigger requires undeclared namespace usage to convert without DOMException/NAMESPACE_ERR.  
CASES: Jsoup element with undeclared prefix/name is treated as a local name during W3C conversion.  
CASES: Nested elements with namespace declarations exercise namespace-stack scope updates.  
CASES: Attributes with declared and undeclared namespace prefixes exercise copyAttributes behavior.  
RISKS: Expected DOM node names/namespaces beyond the trigger are not specified in provided context.