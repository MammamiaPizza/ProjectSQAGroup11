TARGETS: tryFoldSimpleFunctionCall — folding .call() calls, handling first argument, receiver
preservation.
ORACLES: Compare optimized JS output against expected string and AST node structure.
CASES: foo.call(null, a) → foo(a); foo.call(this, a, b) → foo(a, b); call with no extra args.
CASES: obj.method.call(this, x) → must not fold (lose receiver). Test getprop/elem callees.
BOUNDARY: .call() with zero arguments; call target not a NAME (e.g., (expr).call(…)).
ERROR: Incorrect argument list after folding; mishandled first-arg substitution.
RISKS: Folder may discard receiver for property-access callees, altering semantics.
RISKS: Folding may break when the first argument is not null/this or is a complex expression.