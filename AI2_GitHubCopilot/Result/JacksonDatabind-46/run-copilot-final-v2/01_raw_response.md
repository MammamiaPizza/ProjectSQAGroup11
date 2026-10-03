TARGETS=getGenericSignature, buildCanonicalName, getErasedSignature — proper array bracket placement
in signatures.
ORACLES=Array generic signatures must put '[' before element descriptor (JVM spec:
[Ljava/lang/String; not Ljava/lang/String;[]).
CASES=ref to non-array type; ref to String[]; ref to int[]; ref to multi-dim Object[][]; ref to
parameterized array List<String>[].
RISKS=Limited to ReferenceType; base SimpleType may also affect output; unintended regressions for
non-array or wildcard signatures.