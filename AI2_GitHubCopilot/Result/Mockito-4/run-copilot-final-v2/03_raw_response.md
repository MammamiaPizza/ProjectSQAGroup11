package org.mockito.exceptions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.internal.verification.api.VerificationAwareInvocation;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**

 - Tests that demonstrate bug 187: Reporter methods must not throw
 - ClassCastException or NullPointerException when a mock is created with a
 - "bogus" default answer that breaks safelyGetMockName.
  */
 public class ReporterTest {
  private static final String FIELD_NAME = "someField";
  @SuppressWarnings("unused")
  private String someField; // used for reflection
  private final Reporter reporter = new Reporter();
  /**
  - A custom Answer that is not the internal Answers enum.
  - This triggers a ClassCastException in MockUtil.getMockName()
  - for the affected Mockito version (buggy).
    */
   private static final Answer<Object> BOGUS_ANSWER = new Answer<Object>() {
   @Override
   public Object answer(InvocationOnMock invocation) throws Throwable {
   return null;
   }
   };
  interface SomeInterface {
      void doSomething();
  }
  private Object createBogusMock() {
      return Mockito.mock(SomeInterface.class, BOGUS_ANSWER);
  }
  // ----- noMoreInteractionsWanted -----
  @Test(expected = NoInteractionsWanted.class)
  public void noMoreInteractionsWanted_shouldNotThrowClassCastException() {
      Object mockObj = createBogusMock();
      VerificationAwareInvocation undesired = mock(VerificationAwareInvocation.class);
      when(undesired.getMock()).thenReturn(mockObj);
      List<VerificationAwareInvocation> all = Arrays.asList(undesired);
      reporter.noMoreInteractionsWanted(undesired, all);
  }
  // ----- noMoreInteractionsWantedInOrder -----
  @Test(expected = VerificationInOrderFailure.class)
  public void noMoreInteractionsWantedInOrder_shouldNotThrowClassCastException() {
      Object mockObj = createBogusMock();
      Invocation wanted = mock(Invocation.class);
      when(wanted.getMock()).thenReturn(mockObj);
      Invocation previous = mock(Invocation.class);
      when(previous.getMock()).thenReturn(mockObj);
      reporter.noMoreInteractionsWantedInOrder(wanted, previous);
  }
  // ----- cannotInjectDependency -----
  @Test(expected = MockitoException.class)
  public void cannotInjectDependency_shouldNotThrowNullPointerException() throws Exception {
      Field field = ReporterTest.class.getDeclaredField(FIELD_NAME);
      Object matchingMock = createBogusMock();
      Exception details = new RuntimeException("injection failure cause");
      reporter.cannotInjectDependency(field, matchingMock, details);
  }
  // ----- delegatedMethodHasWrongReturnType -----
  @Test(expected = MockitoException.class)
  public void delegatedMethodHasWrongReturnType_shouldNotThrowClassCastException() throws Exception
{
      Object mockObj = createBogusMock();
      Object delegate = new Object();
      Method mockMethod = SomeInterface.class.getMethod("doSomething");
      Method delegateMethod = Object.class.getMethod("toString");
      reporter.delegatedMethodHasWrongReturnType(mockMethod, delegateMethod, mockObj, delegate);
  }
  // ----- invalidArgumentPositionRangeAtInvocationTime -----
  @Test(expected = MockitoException.class)
  public void invalidArgumentPositionRangeAtInvocationTime_shouldNotThrowClassCastException() throws
Exception {
      Object mockObj = createBogusMock();
      InvocationOnMock invocationOnMock = mock(InvocationOnMock.class);
      when(invocationOnMock.getMock()).thenReturn(mockObj);
      when(invocationOnMock.getMethod()).thenReturn(SomeInterface.class.getMethod("doSomething"));
      reporter.invalidArgumentPositionRangeAtInvocationTime(invocationOnMock, false, 5);
  }
  // ----- message content on normal mock -----
  @Test
  public void exceptionMessageShouldContainMockNameWhenPossible() throws Exception {
      Object normalMock = Mockito.mock(SomeInterface.class);
      VerificationAwareInvocation undesired = mock(VerificationAwareInvocation.class);
      when(undesired.getMock()).thenReturn(normalMock);
      List<VerificationAwareInvocation> all = Arrays.asList(undesired);
      try {
          reporter.noMoreInteractionsWanted(undesired, all);
          fail("Expected NoInteractionsWanted");
      } catch (NoInteractionsWanted e) {
          assertNotNull(e.getMessage());
          assertTrue(e.getMessage().contains("SomeInterface"));
      }
  }
  // ----- message fallback on bogus mock -----
  @Test
  public void exceptionMessageShouldUseFallbackWhenMockNameUnavailable() throws Exception {
      Object bogusMock = createBogusMock();
      Field field = ReporterTest.class.getDeclaredField(FIELD_NAME);
      Exception details = new RuntimeException("cause");
      try {
          reporter.cannotInjectDependency(field, bogusMock, details);
          fail("Expected MockitoException");
      } catch (MockitoException e) {
          assertNotNull(e.getMessage());
          // Even when mock name cannot be obtained the message must not be empty.
          assertTrue(e.getMessage().length() > 0);
      }
  }

}