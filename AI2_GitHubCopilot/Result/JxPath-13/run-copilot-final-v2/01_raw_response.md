TARGETS: NamespaceResolver.registerNamespace/getNamespaceURI/getPrefix/clone;
DOMNodePointer.createAttribute/namespacePointer/getNamespaceURI
ORACLES: Trigger test ExternalXMLNamespaceTest.testCreateAndSetAttributeDOM; JXPath usage contract;
JXPathException on unknown prefix
CASES: Register "A","uri" then createAttribute("A:attr") succeeds; unregistered prefix throws;
re-register overwrites map entry
CASES: Parent-resolver fallback; default namespace via getDefaultNamespaceURI; null/empty prefix or
URI inputs
CASES: createAttribute with colon-less name; namespacePointer for known and unknown prefixes; clone
preserves/isolates namespace maps
RISKS: Supplied context lacks fixed source and full test bodies; expected text/message details come
only from bug summary
RISKS: Cannot verify attribute creation side effects or exact exception type beyond "Unknown
namespace prefix: A"