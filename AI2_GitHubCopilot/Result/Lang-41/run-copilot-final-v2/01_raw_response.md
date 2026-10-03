TARGETS: getShortClassName(Class) must render array types as "String[]", not JVM "String;".
TARGETS: getPackageName(Class) must return "java.lang" for String[].class, not "[Ljava.lang".
TARGETS: array handling must cover primitive arrays and multi-dim arrays consistently.
ORACLES: ClassUtilsTest failures define expected: "String[]" and "java.lang".
ORACLES: Raw expected values derive from Class.getName() descriptors (e.g. [Ljava.lang.String;).
CASES: String[].class, int[].class, boolean[].class, String[][].class, Object[].class.
CASES: Non-array String.class, inner class, and primitive int.class as regression guards.
CASES: null and nested array boundaries; empty className for String overload error path.
RISKS: Fix only the array branch; preserve existing non-array/inner class outputs.
RISKS: Avoid generics/type-variable assertions; Class objects carry no parameterized type info.