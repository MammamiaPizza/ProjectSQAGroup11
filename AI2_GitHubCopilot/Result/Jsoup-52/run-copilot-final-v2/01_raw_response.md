TARGETS: XmlDecl.toString/getWholeDecl closing "?>"; XmlDecl attributes quoting; XmlTreeBuilder
token→attrs
ORACLES: XML spec: decl ends "?>"; default double-quoted attribute values; encoding in charset
detection
CASES: "encoding" set/not; single-quoted attrs; extra attrs; empty attrs; BOM+encoding conflict;
missing "?> "
RISKS: No source of private helpers; XML spec conformance assumed; only modified classes available
for test