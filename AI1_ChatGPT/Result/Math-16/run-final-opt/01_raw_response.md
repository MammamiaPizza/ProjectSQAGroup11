TARGETS: FastMath large-argument trigonometric reduction behavior implicated by Math905 trigger tests.  
ORACLES: Trigger assertions require 0.0; current buggy results are Infinity for large positive and negative inputs.  
CASES: Reproduce each trigger’s exact large positive and large negative argument and assert finite 0.0.  
CASES: Add sign-symmetric large-input coverage only where the trigger’s expected result is established.  
RISKS: Context omits trigger bodies, exact input values, and the specific public FastMath method under test.