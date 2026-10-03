TARGETS: org.apache.commons.lang3.StringUtils.replace(String text, String[] searchList, String[]
replacementList)
ORACLES: Expected null-safe return (null text→null, null arrays→no-op or return text) per
commons-lang conventions; Javadoc for replaceEach; existing StringUtilsTesT tests.
CASES: Null searchList, null replacementList, both null, empty arrays, null text, single-element
arrays, overlapping search strings, unequal array lengths, null elements in arrays.
RISKS: Buggy version lacks null array/element guards; desired behavior (return null vs throw) not
explicitly documented; fix may conflict with related replace overloads.