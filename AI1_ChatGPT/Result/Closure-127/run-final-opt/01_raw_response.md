TARGETS: process/visit eliminate unreachable JS while preserving control-flow semantics.  
TARGETS: tryRemoveUnconditionalBranching, computeFollowing, and safe dead-expression/node removal.  
ORACLES: Existing UnreachableCodeEliminationTest assertions are the expected-output source.  
CASES: return followed by statements; ensure unreachable code is removed without altering return behavior.  
CASES: continue followed by statements in loop bodies; retain valid loop control flow.  
CASES: break inside try/finally; do not remove branch required for finally execution.  
CASES: break in try/finally nested in switch; preserve switch and finally control transfer.  
CASES: Issue4177428 variants a/c and return/continue trigger inputs.  
RISKS: AST/output details and helper visibility are not provided; test through CompilerPass process behavior.