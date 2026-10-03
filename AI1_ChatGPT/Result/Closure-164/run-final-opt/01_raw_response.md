TARGETS: ArrowType.getLeastSupertype and getGreatestSubtype for function return types and parameter counts.
TARGETS: ArrowType.isSubtype/isEquivalentTo may affect inferred method-type compatibility and warnings.
ORACLES: FunctionTypeTest expects "function (number, number): boolean", not generic "Function".
ORACLES: TypeCheckTest and LooseTypeCheckTest expect a warning for testMethodInference7.
CASES: Combine arrows whose return types differ and whose parameter counts include two parameters.
CASES: Exercise least-supertype/greatest-subtype results used by method inference.
RISKS: ArrowType is package-private; construction/setup APIs are not provided in this context.
RISKS: Exact warning text, source code, and additional expected type relations are unavailable.