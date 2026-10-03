TARGETS: getShortClassName(Class<?>), getPackageName(Class<?>), including array Class inputs.
ORACLES: Trigger expectations: String[].class -> "String[]"; String[].class package -> "java.lang".
CASES: Reference array String[].class; primitive array int[].class for short name/package consistency.
CASES: Non-array class String.class retains short name "String" and package "java.lang".
RISKS: String overloads/canonical-name helpers are relevant internally, but their expected behavior is not supplied.
RISKS: Do not derive expectations from another program version; null/error behavior is unspecified here.