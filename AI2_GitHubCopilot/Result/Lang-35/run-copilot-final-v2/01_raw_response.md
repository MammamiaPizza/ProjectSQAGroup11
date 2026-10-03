TARGETS: add(T[], T) / addAll(T[], T...) return type correctness for object arrays.
ORACLES: result must be instanceof input's component type (e.g., String[]), not Object[].
CASES: normal: String[]+non-null, String[]+null, empty String[]+element; boundary: null array input.
RISKS: only LANG-571 trigger known; primitive/add(index) overloads not directly indicated.