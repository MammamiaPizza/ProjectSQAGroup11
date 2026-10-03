TARGETS: SubTypeValidator.validateSubType(DeserializationContext, JavaType); hierarchy traversal from type.getRawClass()
TARGETS: SubTypeValidator.instance() singleton may supply validator for direct validation tests
ORACLES: Existing IllegalTypesCheckTest.testJDKTypes1872 and bug report identify NPE as the observed failure
ORACLES: Expected acceptance/rejection details are not provided beyond the trigger failure
CASES: Raw class with superclass chain ending at Object
CASES: Raw interface whose getSuperclass() is null during traversal
CASES: JDK type exercised by testJDKTypes1872, preserving its existing assertion
RISKS: Loop condition dereferences a null class after getSuperclass() returns null
RISKS: Context omits test body and illegal-class-name list, limiting exact expected-result assertions