TARGETS: transform()/TransformDispatcher handling DestructuringAssignment/ParenthesizedExpression
AST nodes
ORACLES: Forbidden destructuring must throw a parse error (e.g., ParseException) or return an error
node
CASES: Invalid destructuring patterns (e.g., grouping without target); valid destructuring to
prevent regression
RISKS: Exact forbidden-pattern set not specified; must not break allowed destructuring (var, for-in,
catch)