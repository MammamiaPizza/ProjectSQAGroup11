TARGETS: DocumentType(String name, String publicId, String systemId, String baseUri); nodeName(); outer HTML rendering
ORACLES: Trigger outerHtmlGeneration expected DOCTYPE preserves publicId quoting before systemId URL
ORACLES: Trigger constructorValidationThrowsExceptionOnBlankName expects IllegalArgumentException for blank name
CASES: Normal name with publicId and systemId; verify exact outerHtml spacing, quotes, and identifiers
CASES: Boundary blank name (including empty/whitespace if accepted by test inputs) throws IllegalArgumentException
RISKS: Only trigger failures and signatures are provided; no broader DocumentType formatting contract is available