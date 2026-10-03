TARGETS: StringUtils.equals(CharSequence, CharSequence), especially differing CharSequence implementations.
ORACLES: Trigger tests expect boolean comparison, not StringIndexOutOfBoundsException.
CASES: Equal String/String and String/non-String CharSequence of equal content and length.
CASES: Same-length differing content; unequal lengths; empty sequences; null inputs if existing tests specify.
RISKS: Context lacks equals contract/source and full existing test expectations; avoid assuming null semantics.