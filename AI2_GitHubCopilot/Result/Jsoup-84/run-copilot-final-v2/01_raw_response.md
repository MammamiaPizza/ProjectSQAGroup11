TARGETS: W3CDom.convert, W3CBuilder.head/updateNamespaces/copyAttributes
ORACLES: Undeclared prefix attrs produce local attrs; no NAMESPACE_ERR from fromJsoup/convert
CASES: local attrs, declared namespace attrs, undeclared prefix (trigger), mixed, nested namespaces,
empty doc
RISKS: Must not assume bug-fixed; validate expected DOM after call using getAttribute/getAttributeNS