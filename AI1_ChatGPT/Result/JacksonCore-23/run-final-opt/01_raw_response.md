TARGETS: DefaultPrettyPrinter.createInstance() behavior for subclass instances  
ORACLES: Trigger test TestDefaultPrettyPrinter.testInvalidSubClass expected failure condition  
CASES: Base DefaultPrettyPrinter createInstance() returns a usable independent printer  
CASES: Subclass not overriding createInstance() must be rejected rather than silently accepted  
RISKS: No trigger source/body or exact expected exception/message is provided  
RISKS: Indentation, separators, and generator output behavior lack supplied expected outputs