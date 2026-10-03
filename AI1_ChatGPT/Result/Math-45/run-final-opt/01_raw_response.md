TARGETS: OpenMapRealMatrix(int,int) dimension-product validation before sparse key computation.
ORACLES: MATH-679 trigger expects NumberIsTooLargeException for an oversized dimension product.
CASES: Normal positive dimensions whose product fits int; construction succeeds with reported dimensions.
CASES: Boundary product equal to Integer.MAX_VALUE; verify accepted if construction is feasible.
CASES: Product just above Integer.MAX_VALUE; expect NumberIsTooLargeException.
RISKS: Large accepted dimensions may be impractical to exercise beyond constructor validation.
RISKS: Available context does not specify exact exception message or argument values.