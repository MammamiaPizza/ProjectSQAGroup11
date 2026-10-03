TARGETS: CodeGenerator string escaping used by CodePrinter for string literals, including jsString/escapeToDoubleQuotedJsString.  
ORACLES: CodePrinterTest::testZero expects NUL printed as "\0", not "\u0000".  
CASES: Print a variable initializer whose string value contains a single NUL character.  
CASES: Verify generated source exactly matches `var x="\0"` for the trigger input.  
RISKS: Only the reported NUL-string rendering behavior is specified; other control-character escaping is unspecified.