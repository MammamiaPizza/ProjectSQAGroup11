TARGETS: StringUtils.equals(CharSequence,CharSequence)
TARGETS: StringUtils.equalsIgnoreCase(CharSequence,CharSequence)
ORACLES: char-by-char content equality; null==null→true; null vs non-null→false; String.equals for
String args
CASES: both empty but different types (e.g., "" vs new StringBuilder())
CASES: both null; one null one empty; both non-null equal content different type
CASES: same type same content; different content; equalsIgnoreCase mixed case
CASES: boundary: very long equal sequences; supplementary characters
RISKS: exact fix unknown; behavior for non-String CharSequence types must be inferred
RISKS: other StringUtils methods unchanged; tests should not rely on no-change behavior as
regression oracles