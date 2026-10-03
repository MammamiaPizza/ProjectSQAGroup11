TARGETS: processProvideCall, ProvidedName.updateMinimumModule, createDeclarationNode, verifyProvide.
ORACLES: Closure Library spec: each goog.provide must declare the namespace; independent modules
each emit a declaration.
CASES: Normal: single module provide. Boundary: provide same namespace in multiple independent
modules. Error: duplicate provide in same module.
RISKS: Unclear whether duplicate provides across modules should merge or stay separate; bug may
cause missing declaration or merge collision.