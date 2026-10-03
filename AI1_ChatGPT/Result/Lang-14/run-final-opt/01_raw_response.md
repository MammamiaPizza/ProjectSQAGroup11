TARGETS: StringUtils.equals(CharSequence, CharSequence), especially non-String CharSequence comparison.
ORACLES: LANG-786 summary and existing StringUtilsEqualsIndexOfTest::testEquals failure.
CASES: Same String instance; equal/different String values; null/null and null/non-null.
CASES: Equal/different StringBuilder or mixed String/StringBuilder with identical content.
CASES: Empty and unequal-length CharSequences; differing character near end.
RISKS: Do not infer behavior for unrelated StringUtils methods from the truncated API context.
