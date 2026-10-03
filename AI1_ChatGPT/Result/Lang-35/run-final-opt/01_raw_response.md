TARGETS: ArrayUtils.add generic object-array overloads, especially null array with non-null element  
ORACLES: LANG-571 trigger expects assignment to String[] without ClassCastException  
CASES: add(null, "x") assigned to String[]; verify result runtime component type is String  
CASES: non-null String[] add preserves element order and runtime array type  
RISKS: Null array/type inference path may allocate Object[] rather than element's array type  
RISKS: Available context omits full add signatures and null/null behavior; avoid asserting unspecified cases