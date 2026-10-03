TARGETS BeanDeserializerFactory Throwable-property construction/access handling during bean deserializer creation.
ORACLES AccessFixTest::testCauseOfThrowableIgnoral and its absence of JsonMappingException.
CASES Deserialize a Throwable subtype under denied suppressAccessChecks permission; verify private cause is ignored.
CASES Normal Throwable deserialization where accessible properties continue to be handled.
RISKS Context omits exact modified method and assertions; derive expectations only from trigger/spec summary.