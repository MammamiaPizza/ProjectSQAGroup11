TARGETS: TypedScopeCreator.defineSlot and isQnameRootedInGlobalScope for qualified names.
TARGETS: Warning emission in TypeCheck for global qualified name inferred but not declared.
ORACLES: JSType expected from pre-built global scope's ObjectType (via getObjectSlot).
CASES: Normal: assign "a.b = 5" inside function; check type of "a.b".
CASES: Boundary: assign "a.b.c" deep qualified name in if-block.
CASES: Error: assign "x.y" when x is not an object in global -> warning.
CASES: Multiple assignments to same global qualified name across scopes.
CASES: Qualified name root is "window" (implicit global) in local scope.
RISKS: Private API (AbstractScopeBuilder, DeferredSetType) not directly testable.
RISKS: Test must manually build global scope with ObjectType slots.