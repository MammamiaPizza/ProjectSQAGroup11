TARGETS: org.jsoup.nodes.Entities.escape(String, Document.OutputSettings) and escape(String,
CharsetEncoder, EscapeMode)
ORACLES: Expected escaped output per HTML spec; entity names are case‑sensitive (e.g., &Uuml; not
&uuml;)
ORACLES: Canonical entity names defined in entity maps (e.g., &Aring; for Å, &aring; for å)
CASES: escape("Å")→"&Aring;", escape("Ü")→"&Uuml;", escape("å")→"&aring;", escape("ü")→"&uuml;"
CASES: Standard named entities: &, <, >; numeric fallback for π→"&#960;" if no name
CASES: Boundary: extended Latin (Æ→&AElig;), Greek; empty string unchanged; all‑ASCII unchanged
RISKS: Entity map content not fully visible; case may differ between base/extended/xhtml EscapeModes
RISKS: Unescape must remain case‑insensitive; regex‑based unescapePattern likely correct already
RISKS: Ensure escape does not double‑encode existing entities or break other output settings