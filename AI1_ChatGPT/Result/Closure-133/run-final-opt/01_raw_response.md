TARGETS: JsDocInfoParser text-block extraction, especially extractMultilineTextualBlock and extractBlockComment  
ORACLES: Trigger testTextExtents; parsed JSDoc text extent positions must not throw IllegalStateException  
CASES: Multi-line JSDoc textual blocks with leading stars, whitespace, line breaks, and closing-comment boundaries  
CASES: Normal single-line and multi-line description extraction; verify text and source extent consistency  
CASES: Boundary empty text, final line without text, and whitespace-only lines near block termination  
RISKS: Extraction helpers are private; test through parser behavior and JSDocInfo/source-position results only  
RISKS: Available context lacks constructor/caller setup and exact expected extents; derive expectations from trigger test