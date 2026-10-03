TARGETS: DataUtil.parseByteData charset detection from XML declarations and BOM/default handling  
TARGETS: XmlDeclaration.getWholeDeclaration/toString attribute serialization and quote normalization  
TARGETS: XmlTreeBuilder.process declaration tokens and declaration attribute parsing  
ORACLES: Trigger expectations: encoding ISO-8859-1 is detected; absent declaration charset defaults UTF-8  
ORACLES: XML output declaration retains/updates encoding and preserves disabled charset-update output  
ORACLES: Parsed <?xml encoding="UTF-8"?> is XmlDeclaration with normalized double-quoted output  
CASES: XML declaration with version and encoding attributes; verify attributes are populated  
CASES: Single-quoted encoding declaration; verify rendered declaration uses expected double quotes  
CASES: No charset, ISO-8859-1, UTF-8, and disabled charset-update document output  
RISKS: Available signatures omit Document charset-update APIs and parser construction details