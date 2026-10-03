TARGETS BeanDeserializerFactory bean eligibility; protected isPotentialBeanType(Class<?>).
ORACLES IllegalTypesCheckTest.testIssue1599 expects an exception message containing "Illegal type".
CASES Exercise the trigger’s illegal-type deserialization path and assert the required message substring.
CASES Cover BeanDeserializerFactory rejection of Proxy classes, whose shown path throws IllegalArgumentException.
RISKS No modified diff/body or trigger input is provided; exact illegal class and normal-path behavior are unavailable.