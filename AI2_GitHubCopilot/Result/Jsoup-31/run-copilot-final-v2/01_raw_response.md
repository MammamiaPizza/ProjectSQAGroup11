TARGETS: XmlTreeBuilder.process(Token): detect Declaration token; Token: add Declaration subclass;
TokeniserState: read <?...?> as Declaration not Comment
ORACLES: Test expects <?xml encoding='UTF-8' ?> → XML Declaration node, not Comment <!--?xml...-->;
output should contain declaration as raw markup
ORACLES: Use existing handlesXmlDeclarationAsDeclaration assertion as expected-result source;
declaration token type must exist and be distinguishable from Comment
CASES: Normal XML declaration <?xml version="1.0"?>; declaration inside body text; missing ?;
attribute-less <?xml?>
CASES: Boundary: empty declaration <?>; declaration with newlines; multiple declarations;
declaration before/after doctype and elements
CASES: Error: unclosed declaration; nested < inside declaration value; whitespace-only attributes;
unexpected end of input during parsing
RISKS: No fixed version provided; must infer correct tokenizer behaviour from test expectation;
ensure HTML mode still treats <?...> as bogus comment
RISKS: Token hierarchy change must not break existing Token unit tests; need to verify Comment and
Character tokens unchanged for HTML parser