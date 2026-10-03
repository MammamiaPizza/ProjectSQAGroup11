TARGETS: tryFoldComparison; operand-order handling for commutative and invertible comparison operators  
ORACLES: Existing trigger tests' folded-expression equality and assertion outcomes  
CASES: Comparison folding covered by testFoldComparison3, including both operand orders  
CASES: Invertible operators must fold equivalently per testInvertibleOperators  
CASES: Commutative operators must fold equivalently per testCommutativeOperators  
RISKS: Methods are private; tests must exercise them through the optimization/compiler pipeline  
RISKS: No source bodies or exact expressions/expected outputs are provided in this context