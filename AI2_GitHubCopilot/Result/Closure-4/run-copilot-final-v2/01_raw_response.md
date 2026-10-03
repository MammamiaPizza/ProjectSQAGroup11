TARGETS: getReferencedType() must detect cycles and avoid infinite recursion.
TARGETS: handleTypeCycle() must replace cyclic NamedType with unknown/resolved type.
TARGETS: finishPropertyContinuations() must not re-trigger invalid resolution after cycle fix.
ORACLES: implements/extends self-loop → parse error cycle, not IMPLEMENTS_NON_INTERFACE.
ORACLES: mutual implements between two interfaces → cycle error, not can-only-implement-interfaces.
ORACLES: getReferencedType() for recursive constructor from interface → no StackOverflowError.
CASES: A extends A (self-cycle), A implements A, A extends B extends A (mutual extends).
CASES: I1 implements I2, I2 implements I1; mixed I extends C implements I.
CASES: Interface-to-constructor conversion triggering deep recursive resolution without cycle mark.
RISKS: private methods require reflection or indirect trigger via TypeRegistry flow.