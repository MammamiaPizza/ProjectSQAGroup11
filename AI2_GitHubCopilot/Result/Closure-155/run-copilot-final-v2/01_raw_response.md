TARGETS: InlineVariables.isVarInlineForbidden, blacklistVarReferencesInTree,
InliningBehavior.inlineValue; ReferenceCollectingCallback.addReference, getReferenceCollection;
Scope.Var.isBleedingFunction
ORACLES: Trigger test assertions (string-compare optimized code); parameter 'a' must not be inlined
when ''arguments'' object is modified or escaped in any reachable scope
CASES: Normal: no arguments access → inlining allowed. Boundary: arguments[0]=... inside if;
arguments passed to function; inner function uses outer arguments; for..in uses arguments; aliased
var=arguments then modified
CASES: Error: arguments assigned to variable then variable passed to another function (indirect
escape); arguments[i] after return (no effect but flagged); arguments used only in dead code?
RISKS: Modified files include Scope, ReferenceCollectingCallback; risk of overly conservative
detection if indirect aliasing not tracked; no full source to see exact criteria for ''modified''