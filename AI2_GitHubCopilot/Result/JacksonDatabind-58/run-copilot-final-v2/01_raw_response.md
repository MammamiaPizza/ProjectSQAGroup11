TARGETS: BeanDeserializerFactory.createBeanDeserializer for Throwable subclasses; skip cause
property
ORACLES: Bug report #877: cause should be ignored; AccessFixTest::testCauseOfThrowableIgnoral
expects no access exception
CASES: Normal: deserialize Throwable (no cause) succeeds; Boundary: Throwable with cause; Error:
strict SecurityManager
RISKS: Only partial API provided; fix likely in property filtering; Java version/security policy may
affect access checks