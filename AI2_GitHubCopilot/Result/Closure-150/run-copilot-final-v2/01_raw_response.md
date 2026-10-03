TARGETS: TypedScopeCreator.createScope, LocalScopeBuilder.visit/handleFunctionInputs, defineName,
StubDeclaration
ORACLES: TypedScopeCreatorTest assertions for expected JSType on namespaced/collected stubs
CASES: "foo.bar = function(){}" namespaced stub; inner named function collected stub; recursion;
nested dots
RISKS: only two test triggers; exact bug behavior confined to stubs in local scopes; global/extern
stubs untold