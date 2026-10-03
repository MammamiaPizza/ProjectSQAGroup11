TARGETS: Node.absUrl(String), relative URL resolution against baseUri.  
ORACLES: Trigger expects base http://jsoup.org/path/file with "?foo" to retain "/path/file?foo".  
CASES: Relative query-only attribute "?foo" resolves using current base document path.  
CASES: Normal relative path and absolute URL resolution; missing/empty attribute behavior if exposed.  
CASES: Base URI ending in file versus directory; query-only URL at path boundary.  
RISKS: Concrete Node construction/API fixtures are not provided; use existing node types only.  
RISKS: No specification for malformed URLs, fragments, null keys, or absent attributes.