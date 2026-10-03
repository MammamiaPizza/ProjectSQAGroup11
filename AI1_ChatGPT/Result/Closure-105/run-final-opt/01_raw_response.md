TARGETS: FoldConstants.process/visit behavior for constant folding of string addition/join expressions.  
ORACLES: Existing FoldConstantsTest.testStringJoinAdd assertion and compiler-transformed output.  
CASES: String + string joins; chained additions and combinations with adjacent string operands.  
CASES: Boundary empty-string operands and grouping/order-sensitive addition expressions.  
RISKS: Addition may involve non-string coercion; expected behavior is not provided beyond the trigger test.  
RISKS: Most relevant folding helpers are private; test through compiler processing/public test harness.