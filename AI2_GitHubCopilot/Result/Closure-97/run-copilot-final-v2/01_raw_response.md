TARGETS: tryFoldShift (specifically >>> unsigned right-shift on negative numbers),
tryFoldBinaryOperator (BITOR/BITAND lines), Node.copyRetainingChildrenFrom onto BITOR/BITAND nodes.

ORACLES: Expected folded results for >>> with negative operands follow JS spec (unsigned right-shift
produces unsigned 32-bit result, not sign-extended).

CASES: >>> with negative left operand + positive small shift (e.g., -1>>>0), >>> on large neg
numbers, >>> shift by 0, edge shift=31, mixed BITAND folding with bitwise ops.

RISKS: Only tryFoldShift/BitAndOr signatures available; no source for Node normalization of
INT/BITNOT. Must rely on JS >>> semantics (ToUint32).