TARGETS: StdDateFormat.setLenient/isLenient and parse(String)/parse(String, ParsePosition)  
ORACLES: Trigger TestStdDateFormat.testLenient; parse result/exception and leniency state  
CASES: Set lenient true/false, then parse ISO-8601, plain date, and RFC1123 inputs  
CASES: Repeated leniency changes before parsing; verify no NullPointerException  
RISKS: Exact accepted invalid-date strings and expected dates are not provided in context  
RISKS: Do not derive expectations from another program version