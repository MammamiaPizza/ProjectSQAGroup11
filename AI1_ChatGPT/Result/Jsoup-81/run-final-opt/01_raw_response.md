TARGETS: DataUtil.parseInputStream/load charset detection from XML declarations when charsetName is absent  
ORACLES: Trigger DataUtilTest::supportsXmlCharsetDeclaration expects “Hellö Wörld!” from XML input  
CASES: UTF-8 XML declaration with non-ASCII text; verify decoded Document text preserves ö  
CASES: Explicit charsetName versus declaration; declaration parsing with quoted charset value  
CASES: Boundary: declaration near input start and BOM-related charset detection  
RISKS: No source/body or full existing tests provided; exact XML declaration handling is limited to trigger evidence