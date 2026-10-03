TARGETS: TypeInference.traverseCall, ensurePropertyDefined, backwardsInferenceFromCallSite;
TypedScopeCreator.resolveTypes, handleFunctionInputs, defineName, getPrototypeOwner
ORACLES: testIssue1023 expects a warning; testMethodBeforeFunction2 expects "function (this:Window,
?): undefined"; testPropertiesOnInterface2 expects no NPE
CASES: method defined before function; interface property definition; missing type warning for
mismatch; forward reference resolution
RISKS: only bug 1023 scope; interaction between scope creator and inference may hide multiple root
causes; NPE source unclear