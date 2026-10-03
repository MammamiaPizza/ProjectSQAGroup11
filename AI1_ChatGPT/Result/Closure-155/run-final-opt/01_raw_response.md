TARGETS: InlineVariables.process; arguments aliases must not be inlined when modified or escaped across scopes.  
TARGETS: ReferenceCollectingCallback reference/block/scope tracking; Scope.Var locality and declaration metadata.  
ORACLES: Existing InlineVariablesTest trigger assertions and compiler output expectations.  
CASES: Outer function modifies arguments; inner function modifies captured arguments.  
CASES: Issue378 argument alias modified before/after use; nested-function mutation.  
CASES: Escaped arguments alias passed/stored in inner contexts, including trigger variants 1,2,4.  
RISKS: APIs shown are largely package-private; test likely needs jscomp package/compiler test harness.  
RISKS: No patch or alternate-version behavior supplied; derive expectations only from existing trigger tests.