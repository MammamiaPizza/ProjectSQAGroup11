TARGETS: DocumentType outerHtml generation of SYSTEM/PUBLIC keywords for doctype
TARGETS: TokeniserState doctype publicIdentifier/systemIdentifier builder population
TARGETS: HtmlTreeBuilderState.InBody anythingElse handling of doctype token insertion
TARGETS: XmlTreeBuilder.process(Token.Doctype) constructing DocumentType node
ORACLES: HTML5 serialisation requires "SYSTEM" before system identifier if present
ORACLES: Existing round-trip test expects doctype SYSTEM keyword to survive parse→emit
CASES: doctype html SYSTEM "dtd" (system-only, must emit keyword)
CASES: doctype html PUBLIC "pub" "dtd" (public+system, both keywords present)
CASES: doctype html "" (empty systemId, force-quirks, no keyword)
RISKS: Only buggy version available; actual fix unknown; rely on test failure evidence