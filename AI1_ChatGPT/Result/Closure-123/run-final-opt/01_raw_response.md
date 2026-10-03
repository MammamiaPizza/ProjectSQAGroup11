TARGETS: CodeGenerator.addExpr and context handling for expressions in for-loop initializers.
ORACLES: CodePrinterTest expected output: for(a=c?0:(0 in d);;)foo().
CASES: Print `in` in a conditional-expression branch within a for initializer.
CASES: Preserve parentheses around `0 in d` where no-in parsing context applies.
CASES: Verify ordinary for initializer/conditional-expression printing remains syntactically valid.
RISKS: Available context is limited to the reported `in` parenthesization failure.