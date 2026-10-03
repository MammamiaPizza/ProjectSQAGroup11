TARGETS: getTypeIfRefinable(Node, FlowScope) — source of "Node cannot be refined" exception
TARGETS: RestrictByTypeOfResultVisitor subclasses (true/false/one) — typeof refinement logic
TARGETS: matchesExpectation(String) — compares typeof result to expected string
ORACLES: getTypeIfRefinable must not throw for non-refinable nodes like "this"
ORACLES: Refined JSType must be a subtype of the original type or null
ORACLES: TypeInferenceTest.testNoThisInference must pass without IllegalArgumentException
CASES: Calling getTypeIfRefinable with a node representing "this" — expect no throw
CASES: typeof refinement for known strings: number, string, boolean, object, function, undefined
CASES: getRestrictedWithoutNull/Undefined with null/void types
RISKS: Concrete visitor logic is hidden; tests must be based on abstract behavior only