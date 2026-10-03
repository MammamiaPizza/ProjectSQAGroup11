TARGETS: org.mockito.internal.MockHandler.handle(Invocation) — verify interaction with verifier when
answer is ThrowsException
ORACLES: expected verifier called on correct mock; failure occurs when extra call to different mock
in same line short-circuits verification
ORACLES: org.mockito.exceptions.base.MockitoAssertionError from never() check; assertion fails
unexpectedly during handle() verification
CASES: verify mock1.method() while mock2.method() also invoked in same expression line; verify-never
on mock1; mixed mock usage chains
CASES: mock1 with ThrowsException answer; handle() processes invocation and delegates to verifier;
order of verification vs extra calls matters
RISKS: limited to MockHandler.handle() internal logic—no full source of verifier interaction or
stubbing internals available for deeper analysis