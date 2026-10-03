TARGETS: Contextual rename inversion; declared-name discovery/replacement across scopes and arguments.  
TARGETS: Normalize duplicate var-declaration removal; NodeUtil support used by these passes.  
ORACLES: Existing trigger assertions define expected transformed AST/source behavior.  
CASES: Inversion cases 3/4: contextual renamed identifiers restore original names correctly.  
CASES: Function arguments: declared parameters and references receive consistent unique/inverted names.  
CASES: Local names with context: nested/local scope renaming preserves contextual distinctions.  
CASES: Duplicate var declarations: remove duplicates without changing declaration semantics.  
RISKS: APIs/expected exact output are truncated; derive assertions only from available test infrastructure.