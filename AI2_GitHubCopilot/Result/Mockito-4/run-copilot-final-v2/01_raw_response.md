TARGETS: safelyGetMockName on mock with bogus default answer; noMoreInteractionsWanted,
noMoreInteractionsWantedInOrder, injectionFailure
ORACLES: Expect NoInteractionsWanted / VerificationInOrderFailure / MockitoException per Javadoc; no
ClassCastException / NPE per bug 187
CASES: mock created with bogus default answer (e.g., custom Answer causing ClassCastException)
CASES: verify that noMoreInteractionsWanted[InOrder] and injectionFailure throw correct exception
types, not ClassCastException or NPE
CASES: normal mock still reports exception with proper mock name in message; mock name must appear
even when default answer is bogus
RISKS: Exact "bogus default answer" not defined; infer from bug triggers that it breaks mock name
retrieval