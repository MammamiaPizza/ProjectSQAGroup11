TARGETS: NodeUtil.mayHaveSideEffects/mayEffectMutableState for OR and HOOK expressions in call contexts.  
TARGETS: PureFunctionIdentifier.process, FunctionAnalyzer.visitCall, and side-effect propagation through calls.  
ORACLES: Existing trigger expectations: MOVABLE for the two ExpressionDecomposer cases.  
ORACLES: PureFunctionIdentifier trigger expected call sets, not empty sets, for OR/HOOK call targets.  
CASES: Calls through `(f||g)`, `(g||g)`, `(f?g:h)`, and `(g?g:k)` expressions.  
CASES: Include OR/HOOK alternatives with side effects and nested calls used by listed triggers.  
CASES: Anonymous function call decomposition must retain a recognized call site.  
RISKS: Available context omits method bodies and complete AST construction/test-helper APIs.