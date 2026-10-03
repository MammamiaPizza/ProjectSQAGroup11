TARGETS: MakeDeclaredNamesUnique scope traversal, declared-name discovery, contextual/inline renamers, and inverter pass.
ORACLES: Existing MakeDeclaredNamesUniqueTest and listed injector/mutator/inlining tests' AST/output assertions.
CASES: Nested scopes with shadowed locals; preserve distinct declarations and consistently rename all references.
CASES: Function parameters, local vars, function declarations/expressions, inner functions, and loop-scoped mutation/inlining.
CASES: Names colliding after inline expansion; repeated inlining and recursive/Y-combinator-like function expressions.
CASES: `arguments`, constants, modified parameters, and mutable call arguments must retain tested inlining semantics.
CASES: Contextual-name inversion: valid generated suffixes restore original names; separator/boundary names remain valid.
RISKS: APIs are mostly private/package-private; test through compiler passes and existing package test infrastructure.
RISKS: No source/body details or exact expected transformed JavaScript provided; derive only from existing test assertions.