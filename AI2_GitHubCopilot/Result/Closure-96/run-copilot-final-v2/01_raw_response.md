TARGETS: TypeCheck.visitCall — argument-count and argument-type validation logic
TARGETS: TypeCheck's JSDoc-based function-signature resolution for call sites
ORACLES: Expected warnings from TypeCheck's warning reporting (existing test infrastructure)
ORACLES: Type annotations from JSDoc (param types, nullability) defining expected argument types
CASES: Call with mismatched argument count (too few / too many) → expect warning
CASES: Call with wrong primitive type (e.g., number where string expected) → expect warning
CASES: Call with null argument where @param demands non-nullable type → expect warning
CASES: Call with interface/union argument that violates declared parameter type
RISKS: Warning message format hard to infer; copy patterns from existing TypeCheckTest