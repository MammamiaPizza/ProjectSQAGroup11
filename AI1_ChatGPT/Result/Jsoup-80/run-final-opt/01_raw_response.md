TARGETS: XmlTreeBuilder.initialiseParse and process(Token), especially XML declaration token handling  
ORACLES: Trigger test handlesDodgyXmlDecl; malformed declaration must not throw IndexOutOfBoundsException  
CASES: Valid XML declaration parses normally through XmlTreeBuilder  
CASES: Dodgy/malformed XML declaration with missing or empty declaration data  
CASES: Declaration-like input at document start and adjacent to ordinary XML content  
RISKS: Token construction and resulting DOM expectations are not provided in the context  
RISKS: No expected parse-error reporting or exact output behavior is specified