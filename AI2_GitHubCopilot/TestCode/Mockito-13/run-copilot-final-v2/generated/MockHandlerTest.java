package org.mockito.internal;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.exceptions.base.MockitoAssertionError;
 import org.mockito.internal.creation.MockSettingsImpl;
 import org.mockito.internal.invocation.Invocation;
 import org.mockito.internal.invocation.InvocationMatcher;
 import org.mockito.internal.invocation.MatchersBinder;
 import org.mockito.internal.progress.MockingProgress;
 import org.mockito.internal.progress.ThreadSafeMockingProgress;
 import org.mockito.internal.stubbing.InvocationContainerImpl;
 import org.mockito.internal.stubbing.StubbedInvocationMatcher;
 import org.mockito.internal.verification.MockAwareVerificationMode;
 import org.mockito.internal.verification.VerificationDataImpl;
 import org.mockito.internal.verification.api.VerificationData;
 import org.mockito.stubbing.Answer;
 import org.mockito.verification.VerificationMode;

 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;

 /**
  * Tests for {@link MockHandler#handle(Invocation)} focusing on verification interactions
  * when verification mode is active and other mocks are invoked in the same expression.
  *
  * <p>Bug 138: VerifyingWithAnExtraCallToADifferentMockTest — verification on one mock
  * fails when an extra call to a different mock occurs in the same line, because the
  * different mock's handle() incorrectly consumes and discards the pending verification mode.
  */
 public class MockHandlerTest {

     private MockHandler<Object> handler;
     private MockingProgress progress;
     private Object mock;
     private Object otherMock;
     private InvocationContainerImpl container;

     @Before
     public void setUp() throws Exception {
         handler = new MockHandler<Object>(new MockSettingsImpl());
         progress = handler.mockingProgress;
         mock = new Object();
         otherMock = new Object();
         container = handler.invocationContainerImpl;
     }

     // ---- Helper to create a mock Invocation ----
     private Invocation createInvocation(Object mockInstance, Method method, Object... args) {
         return new Invocation(mockInstance, method, args, 0);
     }

     // ---- Helper to create a simple stub that returns null ----
     private void stubFindAnswer(MockHandler<?> h, Answer<?> answer) {
         // register an answer via setAnswersForStubbing so findAnswerFor returns it
         h.setAnswersForStubbing(Arrays.<Answer>asList(answer));
         InvocationMatcher dummy = new InvocationMatcher(
                 new Invocation(h, getTestMethod(), new Object[0], 0), (List) new ArrayList());
         h.invocationContainerImpl.setInvocationForPotentialStubbing(dummy);
     }

     private Method getTestMethod() {
         try {
             return Object.class.getMethod("toString");
         } catch (NoSuchMethodException e) {
             throw new RuntimeException(e);
         }
     }

     // ---- 1. Normal invocation without verification returns default answer ----
     @Test
     public void shouldReturnDefaultAnswerWhenNoVerificationPending() throws Throwable {
         Invocation inv = createInvocation(mock, getTestMethod());
         Object result = handler.handle(inv);
         // default answer for MockSettingsImpl is RETURNS_DEFAULTS — null for Object
         assertNull(result);
     }

     // ---- 2. Stubbed invocation returns stubbed answer ----
     @Test
     public void shouldReturnStubbedAnswer() throws Throwable {
         final String expected = "stubbed";
         Answer<String> answer = new Answer<String>() {
             public String answer(InvocationOnMock invocation) {
                 return expected;
             }
         };
         stubFindAnswer(handler, answer);

         Invocation inv = createInvocation(mock, getTestMethod());
         Object result = handler.handle(inv);
         assertEquals(expected, result);
     }

     // ---- 3. Verification mode applied to matching mock executes verification ----
     @Test
     public void shouldDelegateToVerificationModeWhenMockMatches() throws Throwable {
         // A verification mode that throws on verify() so we can detect it was called
         final VerificationMode throwingMode = new VerificationMode() {
             public void verify(VerificationData data) {
                 throw new AssertionError("verification executed");
             }
         };
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(mock, throwingMode);
         progress.verificationStarted(awareMode);

         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null); // no stubbing → default answer path

         try {
             handler.handle(inv);
             fail("Expected verification to be delegated");
         } catch (AssertionError e) {
             assertEquals("verification executed", e.getMessage());
         }
     }

     // ---- 4. Verification mode for different mock is NOT consumed (bug 138 fix) ----
     @Test
     public void shouldNotConsumeVerificationModeForDifferentMock() throws Throwable {
         // Set verification mode targeting otherMock
         VerificationMode realMode = times(1);
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, realMode);
         progress.verificationStarted(awareMode);

         // Invoke handle on a different mock (not otherMock)
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);

         Object result = handler.handle(inv);
         assertNull(result);

         // The verification mode should still be pending (re-added by handle)
         assertNotNull("Verification mode should remain pending for the correct mock",
                 progress.pullVerificationMode());
     }

     // ---- 5. Verification mode is consumed when mock matches ----
     @Test
     public void shouldConsumeVerificationModeWhenMockMatches() throws Throwable {
         VerificationMode mode = times(0);
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(mock, mode);
         progress.verificationStarted(awareMode);

         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);

         handler.handle(inv);

         // After handle on matching mock, verification mode should be consumed
         assertNull("Verification mode should be consumed", progress.pullVerificationMode());
     }

     // ---- 6. Mixed invocations: match-then-mismatch preserves mode for mismatch ----
     @Test
     public void shouldPreserveVerificationModeAcrossMixedInvocations() throws Throwable {
         VerificationMode mode = times(1);
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, mode);
         progress.verificationStarted(awareMode);

         // First call: different mock (not otherMock) — should re-add mode
         Invocation inv1 = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         handler.handle(inv1);

         // Mode should still be available
         assertNotNull("Mode should be re-added after mismatch",
                 progress.pullVerificationMode());

         // Re-set for second test within same method
         progress.verificationStarted(new MockAwareVerificationMode(mock, mode));
         Invocation inv2 = createInvocation(mock, getTestMethod());
         handler.handle(inv2);

         // Now consumed
         assertNull("Mode should be consumed after match",
                 progress.pullVerificationMode());
     }

     // ---- 7. Verification mode is discarded when both mock and mode mismatch ----
     @Test
     public void shouldReAddVerificationModeWhenOnlyMockMismatch() throws Throwable {
         VerificationMode mode = never();
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, mode);
         progress.verificationStarted(awareMode);

         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);

         handler.handle(inv);

         // Mode should still be there (re-added because mock didn't match)
         VerificationMode pulled = progress.pullVerificationMode();
         assertNotNull("Mode should be re-added", pulled);
     }

     // ---- 8. handle with answersForStubbing returns null ----
     @Test
     public void shouldReturnNullWhenAnswersForStubbingPresent() throws Throwable {
         List<Answer> answers = Arrays.<Answer>asList(new Answer<Object>() {
             public Object answer(InvocationOnMock invocation) {
                 return "ignored";
             }
         });
         handler.setAnswersForStubbing(answers);

         Invocation inv = createInvocation(mock, getTestMethod());
         Object result = handler.handle(inv);
         assertNull("Should return null when stubbing in progress", result);
     }

     // ---- 9. ThrowsException answer propagates exception ----
     @Test
     public void shouldPropagateExceptionFromStubbedAnswer() throws Throwable {
         final RuntimeException expected = new RuntimeException("boom");
         Answer<Object> throwingAnswer = new Answer<Object>() {
             public Object answer(InvocationOnMock invocation) {
                 throw expected;
             }
         };
         stubFindAnswer(handler, throwingAnswer);

         Invocation inv = createInvocation(mock, getTestMethod());
         try {
             handler.handle(inv);
             fail("Expected RuntimeException");
         } catch (RuntimeException e) {
             assertSame(expected, e);
         }
     }

     // ---- 10. Boundless verification with no match does not throw unexpectedly ----
     @Test
     public void shouldNotThrowWhenMismatchedMockUsedWithActiveVerification() throws Throwable {
         // Simulates: verify(mock1).method(); mock2.method(); in same expression
         VerificationMode mode = new VerificationMode() {
             public void verify(VerificationData data) {
                 // do nothing — just record it was called (should not be called here)
             }
         };
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, mode);
         progress.verificationStarted(awareMode);

         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);

         // Must not throw; verification is not invoked because mock differs
         Object result = handler.handle(inv);
         assertNull(result);
         assertNotNull("Mode should still be available", progress.pullVerificationMode());
     }

     // ---- 11. Verification with never() on matching mock throws MockitoAssertionError ----
     @Test
     public void shouldThrowMockitoAssertionErrorForNeverVerification() throws Throwable {
         // never() should throw when invocation exists
         VerificationMode neverMode = never();
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(mock, neverMode);
         progress.verificationStarted(awareMode);

         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);

         // The invocation exists (was recorded), so never() should fail
         handler.invocationContainerImpl.setInvocationForPotentialStubbing(
                 new InvocationMatcher(inv, (List) new ArrayList()));

         try {
             handler.handle(inv);
             fail("Expected MockitoAssertionError from never()");
         } catch (MockitoAssertionError e) {
             assertNotNull("Error message must not be null", e.getMessage());
         }
     }

     // ---- 12. Null check on settings constructor ----
     @Test
     public void shouldInitializeMockSettingsCorrectly() {
         MockSettingsImpl settings = new MockSettingsImpl();
         MockHandler<Object> h = new MockHandler<Object>(settings);
         assertNotNull(h.getMockSettings());
         assertSame(settings, h.getMockSettings());
         assertNotNull(h.getInvocationContainer());
     }
 }
