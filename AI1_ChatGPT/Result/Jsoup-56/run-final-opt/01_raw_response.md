TARGETS: DocumentType construction/output and doctype public/system identifier handling during parsing.
TARGETS: HtmlTreeBuilderState, Token.Doctype, TokeniserState, XmlTreeBuilder doctype token flow.
ORACLES: DocumentTypeTest::testRoundTrip expected serialized doctype text.
ORACLES: Expected output preserves SYSTEM before system identifier: `SYSTEM "exampledtdfile.dtd"`.
CASES: Parse and serialize SYSTEM doctype with name `html` and quoted system identifier.
CASES: Round-trip PUBLIC and SYSTEM identifiers; verify identifiers are not omitted or swapped.
CASES: Boundary doctypes with empty public identifier and nonempty system identifier.
RISKS: Context exposes no public parser method signatures beyond modified classes.
RISKS: Do not assert unprovided behavior for malformed doctypes, quirks mode, or XML output.