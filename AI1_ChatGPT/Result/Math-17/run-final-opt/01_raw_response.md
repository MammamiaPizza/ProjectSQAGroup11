TARGETS: Dfp.multiply(Dfp), especially NaN operand classification and exception/trap handling.  
ORACLES: Existing DfpTest.testMultiply is the sole stated expected-result source.  
ORACLES: Trigger expects Multiply #37 result NaN with flags = 1.  
CASES: Reproduce testMultiply’s NaN multiplication vector and assert NaN classification and flags.  
CASES: Cover finite×finite, zero×finite, infinity×finite, and NaN×finite multiplication paths.  
RISKS: Exact #37 operands and DfpField flag-access API are not provided in this context.