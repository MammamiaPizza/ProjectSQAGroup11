TARGETS: TypeCheck.process/processForTesting traversal; visitGetProp property-access diagnostics  
TARGETS: check(Node, boolean) may expose checking behavior for focused AST inputs  
ORACLES: Existing TypeCheckTest.testGetprop4 and testIssue810 require a warning  
CASES: Property access matching testGetprop4 input should emit the expected warning  
CASES: Issue 810 property-access input should emit the expected warning  
CASES: Valid property access should avoid unexpected warnings  
CASES: Boundary receiver/property forms relevant to GETPROP traversal  
RISKS: Warning text/type and compiler setup are not provided in this context  
RISKS: No source body or test inputs are available; derive fixtures from existing tests only