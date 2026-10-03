TARGETS: escapeJava(String), escapeJava(Writer,String); contrast escapeJavaScript only if slash policy differs  
ORACLES: Trigger comparison: escapeJava("... slash (/) ...") preserves "/" without preceding backslash  
CASES: Normal slash in plain text; assert String and StringWriter overload outputs match expected  
CASES: Boundary slash at start/end and repeated slashes; verify no added backslash before each slash  
CASES: Mixed slash with Java escapes (backslash, quote, newline) to ensure slash handling is isolated  
RISKS: Null-input and Writer IOException behavior not specified by supplied context  
RISKS: No use of other versions; expected behavior is limited to LANG-421 trigger evidence