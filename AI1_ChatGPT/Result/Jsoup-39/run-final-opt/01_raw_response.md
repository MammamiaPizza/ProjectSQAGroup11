TARGETS: DataUtil.load(InputStream,...), parseByteData(ByteBuffer,...), and BOM handling with null charsetName  
ORACLES: Trigger expects parsed body text "One" after a spurious byte-order mark, not empty text  
CASES: UTF-8 BOM-prefixed input with no charsetName; assert document text/body retains following content  
CASES: Same BOM-prefixed input with explicit charsetName; verify parsing does not discard content  
CASES: Input without BOM and no charsetName; verify ordinary text parsing remains intact  
RISKS: parseByteData is package-private; tests may need org.jsoup.helper package access  
RISKS: Available context gives no expected behavior for charset extraction, stream limits, or malformed encodings