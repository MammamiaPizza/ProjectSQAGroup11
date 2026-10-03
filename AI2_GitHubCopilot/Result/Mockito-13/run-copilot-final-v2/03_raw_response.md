package org.mockito.internal;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.exceptions.base.MockitoAssertionError;
 import org.mockito.internal.creation.MockSettingsImpl;
 import org.mockito.internal.invocation.Invocation;
 import org.mockito.internal.invocation.InvocationMatcher;
 import org.mockito.internal.invocation.MockitoMethod;
 import org.mockito.internal.invocation.realmethod.RealMethod;
 import org.mockito.internal.progress.MockingProgress;
 import org.mockito.internal.progress.ThreadSafeMockingProgress;
 import org.mockito.internal.stubbing.InvocationContainerImpl;
 import org.mockito.internal.verification.MockAwareVerificationMode;
 import org.mockito.internal.verification.api.VerificationData;
 import org.mockito.stubbing.Answer;
 import org.mockito.verification.VerificationMode;

 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;

 public class MockHandlerTest {

     private MockHandler<Object> handler;
     private MockingProgress progress;
     private Object mock;
     private Object otherMock;
     private InvocationContainerImpl container;

     @Before
     public void setUp() throws Exception {
         handler = new MockHandler<Object>(new MockSettingsImpl());
         progress = ThreadSafeMockingProgress.mockingProgress();
         mock = new Object();
         otherMock = new Object();
         container = (InvocationContainerImpl) handler.getInvocationContainer();
     }

     private Invocation createInvocation(Object mockInstance, Method method, Object... args) {
         MockitoMethod mockitoMethod = new MockitoMethod() {
             public String getName() { return method.getName(); }
             public Class<?> getReturnType() { return method.getReturnType(); }
             public Class<?>[] getParameterTypes() { return method.getParameterTypes(); }
             public Class<?>[] getExceptionTypes() { return method.getExceptionTypes(); }
             public boolean isVarArgs() { return method.isVarArgs(); }
             public Method getJavaMethod() { return method; }
         };
         RealMethod realMethod = new RealMethod() {
             public Object invoke() throws Throwable {
                 return null;
             }
         };
         return new Invocation(mockInstance, mockitoMethod, args, 0, realMethod);
     }

     private void stubFindAnswer(MockHandler<?> h, Answer<?> answer) {
         h.setAnswersForStubbing(Arrays.<Answer>asList(answer));
         InvocationMatcher dummy = new InvocationMatcher(
                 new Invocation(h, null, new Object[0], 0, null),
                 (List) new ArrayList());
         ((InvocationContainerImpl)
h.getInvocationContainer()).setInvocationForPotentialStubbing(dummy);
     }

     private Method getTestMethod() {
         try {
             return Object.class.getMethod("toString");
         } catch (NoSuchMethodException e) {
             throw new RuntimeException(e);
         }
     }

     @Test
     public void shouldReturnDefaultAnswerWhenNoVerificationPending() throws Throwable {
         Invocation inv = createInvocation(mock, getTestMethod());
         Object result = handler.handle(inv);
         assertNull(result);
     }

     @Test
     public void shouldReturnStubbedAnswer() throws Throwable {
         final String expected = "stubbed";
         Answer<String> answer = new Answer<String>() {
             public String answer(org.mockito.invocation.InvocationOnMock invocation) {
                 return expected;
             }
         };
         stubFindAnswer(handler, answer);
         Invocation inv = createInvocation(mock, getTestMethod());
         Object result = handler.handle(inv);
         assertEquals(expected, result);
     }

     @Test
     public void shouldDelegateToVerificationModeWhenMockMatches() throws Throwable {
         final VerificationMode throwingMode = new VerificationMode() {
             public void verify(VerificationData data) {
                 throw new AssertionError("verification executed");
             }
         };
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(mock, throwingMode);
         progress.verificationStarted(awareMode);
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         try {
             handler.handle(inv);
             fail("Expected verification to be delegated");
         } catch (AssertionError e) {
             assertEquals("verification executed", e.getMessage());
         }
     }

     @Test
     public void shouldNotConsumeVerificationModeForDifferentMock() throws Throwable {
         VerificationMode realMode = times(1);
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, realMode);
         progress.verificationStarted(awareMode);
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         Object result = handler.handle(inv);
         assertNull(result);
         assertNotNull("Verification mode should remain pending for the correct mock",
                 progress.pullVerificationMode());
     }

     @Test
     public void shouldConsumeVerificationModeWhenMockMatches() throws Throwable {
         VerificationMode mode = times(0);
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(mock, mode);
         progress.verificationStarted(awareMode);
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         handler.handle(inv);
         assertNull("Verification mode should be consumed", progress.pullVerificationMode());
     }

     @Test
     public void shouldPreserveVerificationModeAcrossMixedInvocations() throws Throwable {
         VerificationMode mode = times(1);
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, mode);
         progress.verificationStarted(awareMode);
         Invocation inv1 = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         handler.handle(inv1);
         assertNotNull("Mode should be re-added after mismatch", progress.pullVerificationMode());
         progress.verificationStarted(new MockAwareVerificationMode(mock, mode));
         Invocation inv2 = createInvocation(mock, getTestMethod());
         handler.handle(inv2);
         assertNull("Mode should be consumed after match", progress.pullVerificationMode());
     }

     @Test
     public void shouldReAddVerificationModeWhenOnlyMockMismatch() throws Throwable {
         VerificationMode mode = never();
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, mode);
         progress.verificationStarted(awareMode);
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         handler.handle(inv);
         VerificationMode pulled = progress.pullVerificationMode();
         assertNotNull("Mode should be re-added", pulled);
     }

     @Test
     public void shouldReturnNullWhenAnswersForStubbingPresent() throws Throwable {
         List<Answer> answers = Arrays.<Answer>asList(new Answer<Object>() {
             public Object answer(org.mockito.invocation.InvocationOnMock invocation) {
                 return "ignored";
             }
         });
         handler.setAnswersForStubbing(answers);
         Invocation inv = createInvocation(mock, getTestMethod());
         Object result = handler.handle(inv);
         assertNull("Should return null when stubbing in progress", result);
     }

     @Test
     public void shouldPropagateExceptionFromStubbedAnswer() throws Throwable {
         final RuntimeException expected = new RuntimeException("boom");
         Answer<Object> throwingAnswer = new Answer<Object>() {
             public Object answer(org.mockito.invocation.InvocationOnMock invocation) {
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

     @Test
     public void shouldNotThrowWhenMismatchedMockUsedWithActiveVerification() throws Throwable {
         VerificationMode mode = new VerificationMode() {
             public void verify(VerificationData data) {
             }
         };
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(otherMock, mode);
         progress.verificationStarted(awareMode);
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         Object result = handler.handle(inv);
         assertNull(result);
         assertNotNull("Mode should still be available", progress.pullVerificationMode());
     }

     @Test
     public void shouldThrowMockitoAssertionErrorForNeverVerification() throws Throwable {
         VerificationMode neverMode = never();
         MockAwareVerificationMode awareMode = new MockAwareVerificationMode(mock, neverMode);
         progress.verificationStarted(awareMode);
         Invocation inv = createInvocation(mock, getTestMethod());
         stubFindAnswer(handler, null);
         container.setInvocationForPotentialStubbing(
                 new InvocationMatcher(inv, (List) new ArrayList()));
         try {
             handler.handle(inv);
             fail("Expected MockitoAssertionError from never()");
         } catch (MockitoAssertionError e) {
             assertNotNull("Error message must not be null", e.getMessage());
         }
     }

     @Test
     public void shouldInitializeMockSettingsCorrectly() {
         MockSettingsImpl settings = new MockSettingsImpl();
         MockHandler<Object> h = new MockHandler<Object>(settings);
         assertNotNull(h.getMockSettings());
         assertSame(settings, h.getMockSettings());
         assertNotNull(h.getInvocationContainer());
     }
 }