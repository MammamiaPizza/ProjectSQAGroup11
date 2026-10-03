TARGETS: toJavaVersionInt(String) returns float instead of int; getJavaVersionAsInt();
isJavaVersionAtLeast(int) callers
ORACLES: int returned should match java.specification.version major*100+minor; test expects 0 for
invalid inputs
CASES: "1.6.0_20"→160?; "1.7.0"→170; "1.5"→150; null→0; empty→0; "Java" invalid→0; system property
current version
RISKS: No test code visible; toVersionInt(int[]) conversion logic unknown; float→int change could
affect isJavaVersionAtLeast(int)