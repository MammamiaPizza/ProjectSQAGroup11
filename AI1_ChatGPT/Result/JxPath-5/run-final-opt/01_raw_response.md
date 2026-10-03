TARGETS: NodePointer.compareTo(Object), exercised while evaluating union of variable and node pointers  
ORACLES: Existing VariableTest.testUnionOfVariableAndNode is the expected-result source  
CASES: Union containing a variable pointer and a node pointer must complete without the reported JXPathException  
CASES: Compare/order pointers participating in the trigger union, including distinct pointer paths '' and '$var'  
RISKS: NodePointer is abstract; tests require existing concrete pointers and JXPath context setup  
RISKS: No modified-code diff or explicit ordering expectation is provided beyond the trigger behavior