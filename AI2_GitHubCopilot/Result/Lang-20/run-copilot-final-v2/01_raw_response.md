TARGETS: join(Object[],char), join(Object[],String), join(Iterator,char/String),
join(Iterable,char/String)
ORACLES: No NPE for null/empty input; return null (array) or empty/appropriate string per existing
StringUtils conventions
CASES: null array → no NPE; empty array → non-null; array with null element → no NPE; null
Iterator/Iterable → no NPE
RISKS: All primitive-array join overloads may share the NPE; null separator could cause second NPE;
boundary separator chars (e.g., \0) safe