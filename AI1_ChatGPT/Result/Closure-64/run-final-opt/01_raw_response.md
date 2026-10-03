TARGETS: Compiler parsing and diagnostic location reporting for ES5 strict mode across multiple inputs  
ORACLES: Trigger test is the expected-result source; reported value must be 17, not -1  
CASES: Multiple JavaScript inputs containing repeated `"use strict"` directives under ES5 strict mode  
CASES: Verify diagnostic/error location is retained for a later input, not lost between inputs  
RISKS: Compile entry point is private; setup likely requires existing CommandLineRunner test infrastructure  
RISKS: Context gives no source diff or diagnostic type/message, so avoid asserting unprovided details