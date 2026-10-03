TARGETS: Normalize.process: removeDuplicateDeclarations for externs/root duplicate vars.  
TARGETS: Normalize.process: ScopeTicklingCallback and local-name uniquification behavior.  
TARGETS: SyntacticScopeCreator.createScope/scanVars redeclaration handling during normalization.  
ORACLES: Existing NormalizeTest assertions for duplicate extern declarations and unique local names.  
ORACLES: AST/output code and compiler change effects exposed by existing test utilities.  
CASES: Duplicate extern var declarations, including duplicates shared with root declarations.  
CASES: Same local identifier across scopes/functions; ensure renamed locals remain distinct.  
CASES: Var redeclarations with and without initializers, preserving assignments/initialization.  
RISKS: APIs are package-private/private; tests likely require existing compiler AST test harness.  
RISKS: Context omits exact expected AST/code and redeclaration semantics beyond trigger names.