TARGETS: DataUtil.load(InputStream,null,baseUri) triggers charset detection from content
TARGETS: parseInputStream must detect charset from XML <?xml encoding="..."?> prolog
ORACLES: Document.body().text() equals "Hellö Wörld!" after load with declared encoding
ORACLES: Document's charset matches the declared encoding (e.g., ISO‑8859‑1)
CASES: Normal: XML with iso‑8859‑1, utf‑8, windows‑1252 encoding declarations
CASES: Boundary: missing encoding declaration → fallback UTF‑8 or HTML default
CASES: Error: invalid encoding name → graceful fallback without crash
CASES: BOM + XML declaration mismatch (e.g., BOM UTF‑8, XML iso‑8859‑1) — which wins?
CASES: Empty input stream — ensure no OOM or infinite loop
RISKS: Buggy code ignores XML charset, test fails; must use Parser.xmlParser() to parse as XML