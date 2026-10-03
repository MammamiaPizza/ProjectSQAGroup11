TARGETS: createNumber(String) precision-preserving type selection (Float vs Double)
ORACLES: Expected type is Double when Float would truncate mantissa; test name implies
Double-equality check
CASES: Values where Float-toString differs from input (e.g., many significant digits or mixed
int/frac)
RISKS: Truncated code hides full Float-vs-Double fallback logic; only attacking test name reveals
intent