TARGETS: FunctionInjector inline decision path: classifyCallSite/CallSiteType, Reference.apply,
CanInlineResult.
ORACLES: testIssue1101a/b expect NO (reject) where bug returns YES; InlineFunctions tests check
unchanged/stable output.
CASES: mutable parameter reassigned/aliased then referenced once vs multiple; call-site type
variants a/b; named vs direct function.
CASES: cost threshold behavior from testCostBasedInlining10; setKnownConstants effects on estimates.
RISKS: Exact mutation/reinsertion conditions absent from prompt; rely only on supplied trigger
names, not extra APIs.
RISKS: canInline/inline signatures not listed; expected values beyond YES/NO for I1101/Cost/Mutable
tests are not specified.