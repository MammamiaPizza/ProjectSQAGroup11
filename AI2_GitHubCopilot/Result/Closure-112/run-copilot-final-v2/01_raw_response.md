TARGETS: backwardsInferenceFromCallSite,inferArguments,updateTypeOfParameters,TemplateTypeReplacer,t
raverseCall,traverseNew
ORACLES: No unexpected TypeCheck warnings on templatized code; tests testIssue1058/11 define
expected no-warning outcomes
CASES: instanceof with generic type (regression 1058), templatized fn returning template param,
multiple type params, nested generics, null/union inputs
CASES: unresolved-template boundary, method overloads with generics, narrowing after instanceof,
cross‑scope use of narrowed template type
RISKS: Only buggy-version API; no source; relying on test failures for expected; fix‑side effects
unknown