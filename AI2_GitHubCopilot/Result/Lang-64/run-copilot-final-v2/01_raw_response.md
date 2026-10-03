TARGETS: ValuedEnum.compareTo(Object) method behavior with incompatible enum types.
ORACLES: Java Comparable contract: compareTo() must throw ClassCastException on incompatible types.
CASES: Compare same-class ValuedEnum by value; compare with null; compare with another Enum subclass
(should throw ClassCastException).
RISKS: Only one test is provided; null-handling spec unclear; value-ordering edge cases may be
missed.