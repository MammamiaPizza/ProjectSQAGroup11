TARGETS: TokenStream.isKeyword(String), TokenStream.isJSIdentifier(String)  
ORACLES: Existing ConvertToDottedPropertiesTest::testQuotedProps and ::testDoNotConvert assertions  
CASES: Identifier property names that should permit quoted-to-dotted conversion  
CASES: Keyword property names that must remain quoted (including class, final, public, interface, protected)  
CASES: Normal non-keyword identifiers versus keyword-like prefixes/suffixes  
CASES: Boundary short names and exact keyword lengths used by isKeyword matching  
RISKS: Keyword recognition fall-through/partial branch logic may misclassify names  
RISKS: No method bodies or assertion expected strings are provided; derive expected results only from tests