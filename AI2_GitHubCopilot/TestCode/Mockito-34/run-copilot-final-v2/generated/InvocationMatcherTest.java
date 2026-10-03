package org.mockito.internal.invocation;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.lang.reflect.Method;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;

 import org.hamcrest.Matcher;
 import org.junit.Test;
 import org.mockito.Mockito;
 import org.mockito.internal.invocation.Invocation;
 import org.mockito.internal.invocation.InvocationMatcher;
 import org.mockito.internal.matchers.CapturesArguments;

 @SuppressWarnings("unchecked")
 public class InvocationMatcherTest {

     @Test
     public void shouldMatchWhenArgumentsAndMatchersCountEqual() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         Matcher<Object> m1 = mock(Matcher.class);
         Matcher<Object> m2 = mock(Matcher.class);
         when(m1.matches("a")).thenReturn(true);
         when(m2.matches("b")).thenReturn(true);

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation, Arrays.asList(m1,
m2));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getMock()).thenReturn(mock);
         when(actualInvocation.getMethod()).thenReturn(method);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"a", "b"});

         assertTrue(matcher.matches(actualInvocation));
     }

     @Test
     public void shouldNotMatchWhenActualArgsCountLessThanMatchersCount() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         Matcher<Object> m1 = mock(Matcher.class);
         Matcher<Object> m2 = mock(Matcher.class);

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation, Arrays.asList(m1,
m2));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getMock()).thenReturn(mock);
         when(actualInvocation.getMethod()).thenReturn(method);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"a"}); // fewer args

         // Buggy code would throw ArrayIndexOutOfBoundsException here; desired: false
         assertFalse(matcher.matches(actualInvocation));
     }

     @Test
     public void shouldNotMatchWhenActualArgsCountMoreThanMatchersCount() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         Matcher<Object> m1 = mock(Matcher.class);

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation, Arrays.asList(m1));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getMock()).thenReturn(mock);
         when(actualInvocation.getMethod()).thenReturn(method);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"a", "b"}); // more args

         assertFalse(matcher.matches(actualInvocation));
     }

     @Test
     public void shouldNotThrowWhenMatchingWithZeroActualArgs() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         Matcher<Object> m1 = mock(Matcher.class);
         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation, Arrays.asList(m1));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getMock()).thenReturn(mock);
         when(actualInvocation.getMethod()).thenReturn(method);
         when(actualInvocation.getArguments()).thenReturn(new Object[0]); // zero

         // should not throw, should return false
         assertFalse(matcher.matches(actualInvocation));
     }

     @Test
     public void shouldCaptureArgumentsWhenActualArgsCountMatches() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         CapturingMatcher cm1 = new CapturingMatcher();
         CapturingMatcher cm2 = new CapturingMatcher();

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation,
Arrays.<Matcher>asList(cm1, cm2));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"x", "y"});

         matcher.captureArgumentsFrom(actualInvocation);

         assertEquals("x", cm1.captured);
         assertEquals("y", cm2.captured);
     }

     @Test
     public void shouldCaptureArgumentsSafelyWhenActualArgsFewerThanMatchers() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         CapturingMatcher cm1 = new CapturingMatcher();
         CapturingMatcher cm2 = new CapturingMatcher();

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation,
Arrays.<Matcher>asList(cm1, cm2));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"x"}); // only one arg

         // Should capture the first arg and NOT throw ArrayIndexOutOfBoundsException for the second
         matcher.captureArgumentsFrom(actualInvocation);

         assertEquals("x", cm1.captured);
         assertNull(cm2.captured); // not called
     }

     @Test
     public void shouldCaptureArgumentsSafelyWhenActualArgsMoreThanMatchers() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         CapturingMatcher cm1 = new CapturingMatcher();

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation,
Arrays.<Matcher>asList(cm1));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"a", "b"}); // extra args

         matcher.captureArgumentsFrom(actualInvocation);

         assertEquals("a", cm1.captured);
     }

     @Test
     public void shouldCaptureArgumentsSafelyWithZeroActualArgs() throws Exception {
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         CapturingMatcher cm1 = new CapturingMatcher();

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation,
Arrays.<Matcher>asList(cm1));

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getArguments()).thenReturn(new Object[0]);

         // Must not throw ArrayIndexOutOfBoundsException
         matcher.captureArgumentsFrom(actualInvocation);

         assertNull(cm1.captured);
     }

     @Test
     public void shouldSimilarMethodNotThrowWhenCandidateArgsCountMismatches() throws Exception {
         Object mock = new Object();
         // same method name but different Method objects (overloaded)
         Method method1 = String.class.getMethod("valueOf", Object.class);
         Method method2 = String.class.getMethod("valueOf", int.class);

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method1);

         Matcher<Object> m1 = mock(Matcher.class);
         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation, Arrays.asList(m1));

         Invocation candidate = mock(Invocation.class);
         when(candidate.getMock()).thenReturn(mock);
         when(candidate.getMethod()).thenReturn(method2);
         when(candidate.isVerified()).thenReturn(false);
         // candidate arguments length differs from matchers size -> bug would cause
ArrayIndexOutOfBoundsException
         when(candidate.getArguments()).thenReturn(new Object[]{"a", "b"});

         // hasSimilarMethod should catch the exception inside safelyArgumentsMatch and return true
         assertTrue("should be similar when overloaded but args mismatch",
matcher.hasSimilarMethod(candidate));
     }

     @Test
     public void shouldMatchWithMatchersBuiltFromInvocationWhenEmptyMatchersSupplied() throws
Exception {
         // When an empty matcher list is supplied, the constructor delegates to
invocation.argumentsToMatchers()
         Object mock = new Object();
         Method method = Object.class.getMethod("toString");

         Invocation matcherInvocation = mock(Invocation.class);
         when(matcherInvocation.getMock()).thenReturn(mock);
         when(matcherInvocation.getMethod()).thenReturn(method);

         Matcher<Object> autoMatcher = mock(Matcher.class);
         when(autoMatcher.matches("val")).thenReturn(true);
         when(matcherInvocation.argumentsToMatchers()).thenReturn(Collections.<Matcher>singletonList
(autoMatcher));

         InvocationMatcher matcher = new InvocationMatcher(matcherInvocation); // empty list -> uses
argumentsToMatchers

         Invocation actualInvocation = mock(Invocation.class);
         when(actualInvocation.getMock()).thenReturn(mock);
         when(actualInvocation.getMethod()).thenReturn(method);
         when(actualInvocation.getArguments()).thenReturn(new Object[]{"val"});

         assertTrue(matcher.matches(actualInvocation));
         // Also verify the matcher list was populated
         assertEquals(1, matcher.getMatchers().size());
         assertSame(autoMatcher, matcher.getMatchers().get(0));
     }

     // Helper: a Hamcrest Matcher that also implements CapturesArguments for testing
captureArgumentsFrom.
     private static class CapturingMatcher extends org.hamcrest.BaseMatcher<Object> implements
CapturesArguments {

         Object captured;

         @Override
         public boolean matches(Object item) {
             return true;
         }

         @Override
         public void describeTo(org.hamcrest.Description description) {
         }

         @Override
         public void captureFrom(Object argument) {
             this.captured = argument;
         }
     }
 }
