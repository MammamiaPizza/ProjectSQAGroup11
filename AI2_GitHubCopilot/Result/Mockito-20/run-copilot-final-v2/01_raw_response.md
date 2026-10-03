TARGETS: ByteBuddyMockMaker.createMock; constructor-driven mock/spy creation; inner-class handling
TARGETS: initializeClassInstantiator; ensureMockIsAssignableToMockedType; describeClass messaging
ORACLES: test assertions expect constructor-set values ("hey!", "inner strength") not null
ORACLES: SpyAnnotationTest/CreatingMocksWithConstructorTest failure messages define expected
outcomes
CASES: spy inner class retains "inner strength" field from constructor
CASES: create mock with explicit constructor yields "hey!" field value
CASES: mocking/spying abstract classes returns non-null instance
CASES: inner class with wrong outer instance must throw expected assertion error
CASES: explosive constructor must emit expected failure exception/message
RISKS: limited to trigger signatures; exact expected method behavior must be inferred from tests