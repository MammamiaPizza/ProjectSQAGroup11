TARGETS: CodeConsumer.append(String) output handling relevant to numeric literal emission.  
TARGETS: CodeConsumer.getLastChar() and isWordChar(char) may govern token separation/formatting.  
ORACLES: Trigger expects generated code `var x=-0.0` for issue 582.  
ORACLES: JUnit ComparisonFailure provides exact expected versus actual output.  
CASES: Emit negative zero decimal literal; preserve `-0.0` rather than dropping sign/decimal.  
CASES: Compare positive zero and ordinary negative numeric output for regression boundaries.  
RISKS: Context omits concrete CodeConsumer subclass/call path and numeric-printing API.