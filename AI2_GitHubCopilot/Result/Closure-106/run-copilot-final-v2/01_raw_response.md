TARGETS: GlobalNamespace.isGlobalNameReference, isGlobalVarReference, process; Ref.markTwins;
BuildGlobalNamespace.visit
ORACLES: TestJSComp expectations in testJSDocComments, CollapsePropertiesTest assertions on name
collapsing/cancellation
ORACLES: JSDocInfoBuilder.recordNoSideEffects populating known annotation; builder record*
side-effects visible in JSDocInfo
CASES: JSDoc comment on statement prevents "useless code" diagnostic (normal); nested assignment in
property reference (edge, crash)
CASES: Comma-operator in property-value path triggers IllegalArgumentException (error); twin refs
with shared child namespace (boundary)
CASES: Twin references on parent cancel child collapsing (normal); single-ref child still
collapsible (boundary)
RISKS: JSDocInfo loss or misassociation across GlobalNamespace passes; Ref twin marking may leave
stale state for collapsing
RISKS: IllegalArgumentException crash-path depends on node parent/child structure not validated
before collapsing transform