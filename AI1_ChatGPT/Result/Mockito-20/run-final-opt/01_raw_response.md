TARGETS: ByteBuddyMockMaker.createMock; constructor-based creation, spies, abstract and inner classes.  
TARGETS: ensureMockIsAssignableToMockedType; validate created proxy assignability to requested type.  
ORACLES: Trigger assertions: constructor-initialized methods return "hey!" and inner spy returns "inner strength".  
ORACLES: Trigger failure assertions define expected errors for explosive/missing constructors and wrong outer instance.  
CASES: Mock concrete/abstract types with constructor; verify constructor state is retained, not null/default.  
CASES: Spy an inner class; mock inner class with valid outer instance; verify initialized behavior.  
CASES: Explosive constructor, missing constructor, and mismatched outer instance must report expected failure.  
RISKS: Exact exception types/messages are not provided; derive only from existing trigger assertions.  
RISKS: No source/body details for settings, instantiator, or constructor-selection behavior are available.