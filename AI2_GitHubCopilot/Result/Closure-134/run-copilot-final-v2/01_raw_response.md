TARGETS: AmbiguateProperties.process, recordProperty, maybeMarkCandidate, computeRelatedTypes
TARGETS: TypedScopeCreator.AbstractScopeBuilder.defineSlot, CollectProperties.visit
ORACLES: TypeCheck expects warning for malformed @implements/@extends usage (testIssue86)
ORACLES: AmbiguateProperties should preserve distinct property names across implements/extends
chains
CASES: Normal: class A implements I; interface I has prop p; class B extends A overrides p
CASES: Boundary: multiple interfaces declare same property; property used in both extends and
implements levels
CASES: Error: @implements references non-interface; interface method signature mismatch; missing
JSDoc type
RISKS: Test oracles derived from regression test names only; exact warning text unknown
RISKS: No access to full compilation pipeline; type inference may depend on unimplemented operations