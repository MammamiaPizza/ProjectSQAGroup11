TARGETS: GlobalNamespace collection must recognize catch-bound exception names as locally scoped, not globals.  
ORACLES: Existing trigger expects no JSC_UNDEFINED_NAME warning for catch variable e at its use.  
CASES: Compile code with try/catch(e){ e; } and run CheckGlobalNames warning validation.  
CASES: Include catch variable use in nested expressions/functions to verify scope handling.  
CASES: Contrast truly undeclared global reference outside catch, if existing diagnostics support it.  
RISKS: GlobalNamespace APIs mostly lazy/private; test via compiler pass behavior rather than internals.  
RISKS: Context lacks constructor/setup signatures and exact warning assertion helpers.