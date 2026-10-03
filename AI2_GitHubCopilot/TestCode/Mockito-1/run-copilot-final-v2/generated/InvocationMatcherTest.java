package org.mockito.internal.invocation;

 import static org.junit.Assert.*;

 import java.lang.reflect.Method;
 import java.util.*;

 import org.hamcrest.Matcher;
 import org.junit.Test;
 import org.mockito.internal.matchers.CapturesArguments;
 import org.mockito.internal.matchers.VarargMatcher;
 import org.mockito.invocation.Invocation;
 import org.mockito.invocation.Location;

 public class InvocationMatcherTest {

     /* ---------- Helper stubs ---------- */

     private static Invocation createInvocation(final Object mock, final Method method,
                                                final Object[] rawArguments) {
         return new Invocation() {
             @Override public Object getMock() { return mock; }
             @Override public Method getMethod() { return method; }
             @Override public Object[] getArguments() { return rawArguments; }
             @Override public Object getRawArguments() { return rawArguments; }
             @Override public boolean isVerified() { return false; }
             @Override public int getSequenceNumber() { return 0; }
             @Override public Location getLocation() { return null; }
             @Override public Object getArgumentAt(int index, Class<?> clazz) {
                 return rawArguments[index];
             }
             @Override public void markVerified() { }
             @Override public boolean isIgnoredForVerification() { return false; }
             @Override public void ignoreForVerification() { }
         };
     }

     private static class CapturesArgumentsMatcher implements Matcher<Object>, CapturesArguments {
         private final List<Object> captured = new ArrayList<Object>();
         @Override public boolean matches(Object item) { return true; }
         @Override public void captureFrom(Object argument) { captured.add(argument); }
         public List<Object> getCaptured() { return captured; }
     }

     private static class VarargCapturesMatcher implements Matcher<Object>, CapturesArguments,
VarargMatcher {
         private final List<Object> captured = new ArrayList<Object>();
         @Override public boolean matches(Object item) { return true; }
         @Override public void captureFrom(Object argument) { captured.add(argument); }
         public List<Object> getCaptured() { return captured; }
     }

     private static class AlwaysTrueMatcher implements Matcher<Object> {
         @Override public boolean matches(Object item) { return true; }
     }

     public static class SampleMethods {
         public void nonVararg(String a, String b) {}
         public void vararg(String... args) {}
         public void overloaded(String s) {}
         public void overloaded(Object o) {}
     }

     private static Method varargMethod() throws NoSuchMethodException {
         return SampleMethods.class.getMethod("vararg", String[].class);
     }

     private static Method nonVarargMethod() throws NoSuchMethodException {
         return SampleMethods.class.getMethod("nonVararg", String.class, String.class);
     }

     private static Method overloadedStringMethod() throws NoSuchMethodException {
         return SampleMethods.class.getMethod("overloaded", String.class);
     }

     private static Method overloadedObjectMethod() throws NoSuchMethodException {
         return SampleMethods.class.getMethod("overloaded", Object.class);
     }

     /* ---------- captureArgumentsFrom tests ---------- */

     @Test
     public void shouldCaptureArgumentsForNonVarargMethod() throws Exception {
         Invocation invocation = createInvocation(new Object(), nonVarargMethod(),
                 new Object[]{"a", "b"});
         CapturesArgumentsMatcher m1 = new CapturesArgumentsMatcher();
         CapturesArgumentsMatcher m2 = new CapturesArgumentsMatcher();
         InvocationMatcher im = new InvocationMatcher(invocation, Arrays.<Matcher>asList(m1, m2));

         im.captureArgumentsFrom(invocation);

         assertEquals(Arrays.asList("a"), m1.getCaptured());
         assertEquals(Arrays.asList("b"), m2.getCaptured());
     }

     @Test
     public void shouldCaptureVarargsWhenVarargMatcherIsUsed() throws Exception {
         // rawArguments: last element is the vararg array
         Object[] rawArgs = new Object[]{new String[]{"x", "y"}};
         Invocation invocation = createInvocation(new Object(), varargMethod(), rawArgs);
         VarargCapturesMatcher varargMatcher = new VarargCapturesMatcher();
         InvocationMatcher im = new InvocationMatcher(invocation,
                 Arrays.<Matcher>asList(varargMatcher));

         // Buggy version throws UnsupportedOperationException here
         im.captureArgumentsFrom(invocation);

         // When fixed, the vararg matcher should have captured both vararg elements
         assertEquals(Arrays.asList("x", "y"), varargMatcher.getCaptured());
     }

     @Test
     public void shouldCaptureVarargsAsArrayWhenNoVarargMatcherUsed() throws Exception {
         // rawArguments: last element is the vararg array
         String[] varargArray = new String[]{"hello"};
         Object[] rawArgs = new Object[]{varargArray};
         Invocation invocation = createInvocation(new Object(), varargMethod(), rawArgs);
         CapturesArgumentsMatcher matcher = new CapturesArgumentsMatcher();
         InvocationMatcher im = new InvocationMatcher(invocation,
                 Arrays.<Matcher>asList(matcher));

         // Buggy version throws UnsupportedOperationException
         im.captureArgumentsFrom(invocation);

         // When fixed, the non-vararg matcher should capture the raw array itself
         assertEquals(Arrays.<Object>asList(varargArray), matcher.getCaptured());
     }

     @Test
     public void shouldNotThrowWhenArgsCountDoesNotMatchMatchersForVarargs() throws Exception {
         // rawArgs has only 1 element (the vararg array), but we supply 2 matchers
         Object[] rawArgs = new Object[]{new String[]{"a", "b"}};
         Invocation invocation = createInvocation(new Object(), varargMethod(), rawArgs);
         CapturesArgumentsMatcher m1 = new CapturesArgumentsMatcher();
         CapturesArgumentsMatcher m2 = new CapturesArgumentsMatcher();
         InvocationMatcher im = new InvocationMatcher(invocation,
                 Arrays.<Matcher>asList(m1, m2));

         // Buggy version throws UnsupportedOperationException regardless of match count
         try {
             im.captureArgumentsFrom(invocation);
             // if we reach here, the bug is fixed; we don't assert capture because
             // behaviour for mismatched counts is undefined, but it must not throw
         } catch (UnsupportedOperationException e) {
             fail("Bug: captureArgumentsFrom threw UnsupportedOperationException for varargs");
         }
     }

     /* ---------- matches tests ---------- */

     @Test
     public void shouldMatchWhenSameMockSameMethodAndArguments() throws Exception {
         Object mock = new Object();
         Invocation inv = createInvocation(mock, nonVarargMethod(),
                 new Object[]{"a", "b"});
         InvocationMatcher im = new InvocationMatcher(inv,
                 Arrays.<Matcher>asList(new AlwaysTrueMatcher(), new AlwaysTrueMatcher()));

         assertTrue(im.matches(inv));
     }

     @Test
     public void shouldNotMatchWhenMocksDiffer() throws Exception {
         Invocation inv1 = createInvocation(new Object(), nonVarargMethod(),
                 new Object[]{"a", "b"});
         Invocation inv2 = createInvocation(new Object(), nonVarargMethod(),
                 new Object[]{"a", "b"});
         InvocationMatcher im = new InvocationMatcher(inv1,
                 Arrays.<Matcher>asList(new AlwaysTrueMatcher(), new AlwaysTrueMatcher()));

         assertFalse(im.matches(inv2));
     }

     @Test
     public void shouldNotMatchWhenArgumentsDiffer() throws Exception {
         Object mock = new Object();
         Invocation inv1 = createInvocation(mock, nonVarargMethod(),
                 new Object[]{"a", "b"});
         Invocation inv2 = createInvocation(mock, nonVarargMethod(),
                 new Object[]{"x", "y"});
         // matchers that check equality: for "a" and "b"
         Matcher<String> eqA = new org.hamcrest.core.IsEqual<String>("a");
         Matcher<String> eqB = new org.hamcrest.core.IsEqual<String>("b");
         @SuppressWarnings("unchecked")
         List<Matcher> matchers = Arrays.<Matcher>asList(eqA, eqB);
         InvocationMatcher im = new InvocationMatcher(inv1, matchers);

         assertFalse(im.matches(inv2));
     }

     /* ---------- hasSameMethod tests ---------- */

     @Test
     public void hasSameMethodShouldReturnTrueForIdenticalSignature() throws Exception {
         Invocation inv = createInvocation(new Object(), nonVarargMethod(), null);
         InvocationMatcher im = new InvocationMatcher(inv);

         assertTrue(im.hasSameMethod(inv));
     }

     @Test
     public void hasSameMethodShouldReturnFalseForDifferentParameterTypes() throws Exception {
         Invocation inv1 = createInvocation(new Object(), nonVarargMethod(), null);
         Invocation inv2 = createInvocation(new Object(), overloadedObjectMethod(), null);
         InvocationMatcher im = new InvocationMatcher(inv1);

         assertFalse(im.hasSameMethod(inv2));
     }

     @Test
     public void hasSameMethodShouldReturnFalseForSameNameButDifferentParamCount() throws Exception
{
         Invocation inv1 = createInvocation(new Object(), nonVarargMethod(), null);
         Invocation inv2 = createInvocation(new Object(), varargMethod(), null);
         InvocationMatcher im = new InvocationMatcher(inv1);

         assertFalse(im.hasSameMethod(inv2));
     }

     /* ---------- hasSimilarMethod tests ---------- */

     @Test
     public void hasSimilarMethodShouldReturnTrueForSameUnverifiedInvocation() throws Exception {
         Invocation inv = createInvocation(new Object(), nonVarargMethod(), null);
         InvocationMatcher im = new InvocationMatcher(inv);

         assertTrue(im.hasSimilarMethod(inv));
     }

     @Test
     public void hasSimilarMethodShouldReturnFalseForOverloadedButSameArguments() throws Exception {
         Object mock = new Object();
         // overloaded(Object) and overloaded(String) have same args when called with "x"
         Invocation inv1 = createInvocation(mock, overloadedStringMethod(),
                 new Object[]{"x"});
         Invocation inv2 = createInvocation(mock, overloadedObjectMethod(),
                 new Object[]{"x"});
         InvocationMatcher im = new InvocationMatcher(inv1);

         assertFalse(im.hasSimilarMethod(inv2));
     }

     /* ---------- createFrom test ---------- */

     @Test
     public void createFromShouldReturnMatchingNumberOfMatchers() throws Exception {
         Invocation inv = createInvocation(new Object(), nonVarargMethod(), null);
         List<Invocation> invocations = Arrays.asList(inv, inv, inv);

         List<InvocationMatcher> result = InvocationMatcher.createFrom(invocations);

         assertEquals(3, result.size());
     }
 }
