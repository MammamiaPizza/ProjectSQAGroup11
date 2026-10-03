package org.mockito.internal.invocation;

 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.mockito.Mockito.mock;
 import static org.mockito.Mockito.when;

 import java.lang.reflect.Method;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;

 import org.hamcrest.BaseMatcher;
 import org.hamcrest.Description;
 import org.hamcrest.Matcher;
 import org.junit.Test;
 import org.mockito.Matchers;

 public class InvocationMatcherTest {

     // Helper class for standard method lookups
     public static class Helper {
         public String echo(String s) { return s; }
         public int add(int a, int b) { return a + b; }
     }

     // Generic interface for bridge-method scenarios
     interface GenericInterface<T> {
         T apply(T input);
     }
     static class Concrete implements GenericInterface<String> {
         public String apply(String input) { return input; }
     }

     private static final Object MOCK_A = new Object();
     private static final Object MOCK_B = new Object();

     // ---------- helpers ----------

     private Invocation createInvocation(Object mock, Method method, Object[] args, boolean
verified) {
         Invocation inv = mock(Invocation.class);
         when(inv.getMock()).thenReturn(mock);
         when(inv.getMethod()).thenReturn(method);
         when(inv.getArguments()).thenReturn(args);
         when(inv.isVerified()).thenReturn(verified);
         return inv;
     }

     private boolean invokeSafelyArgumentsMatch(InvocationMatcher matcher, Object[] actualArgs)
throws Exception {
         Method m = InvocationMatcher.class.getDeclaredMethod("safelyArgumentsMatch",
Object[].class);
         m.setAccessible(true);
         return (Boolean) m.invoke(matcher, (Object) actualArgs);
     }

     // ---------- matches ----------

     @Test
     public void matches_exactSameMethod_shouldReturnTrue() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"a"}, false);
         Invocation actual = createInvocation(MOCK_A, method, new Object[]{"b"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyString());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         assertTrue(matcher.matches(actual));
     }

     @Test
     public void matches_bridgeMethodWithGenerics_shouldReturnTrue() throws Exception {
         // Wanted method from the generic interface, actual from the concrete class.
         // Method.equals() returns false for such bridge pairs.
         Method interfaceMethod = GenericInterface.class.getMethod("apply", Object.class);
         Method concreteMethod = Concrete.class.getMethod("apply", String.class);

         Invocation wanted = createInvocation(MOCK_A, interfaceMethod, new Object[]{"x"}, false);
         Invocation actual = createInvocation(MOCK_A, concreteMethod, new Object[]{"x"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyObject());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);

         // Bug assertion: should be true even with generics erasure / bridge methods
         assertTrue(matcher.matches(actual));
     }

     @Test
     public void matches_differentMock_shouldReturnFalse() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"a"}, false);
         Invocation actual = createInvocation(MOCK_B, method, new Object[]{"a"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyString());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         assertFalse(matcher.matches(actual));
     }

     @Test
     public void matches_differentMethodName_shouldReturnFalse() throws Exception {
         Method echoMethod = Helper.class.getMethod("echo", String.class);
         Method addMethod = Helper.class.getMethod("add", int.class, int.class);
         Invocation wanted = createInvocation(MOCK_A, echoMethod, new Object[]{"a"}, false);
         Invocation actual = createInvocation(MOCK_A, addMethod, new Object[]{1, 2}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyString());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         assertFalse(matcher.matches(actual));
     }

     @Test
     public void matches_argumentsMismatch_shouldReturnFalse() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"expected"}, false);
         Invocation actual = createInvocation(MOCK_A, method, new Object[]{"different"}, false);

         // a matcher that only matches "expected"
         Matcher<String> exact = new BaseMatcher<String>() {
             public boolean matches(Object item) { return "expected".equals(item); }
             public void describeTo(Description description) { }
         };
         List<Matcher> matchers = Collections.<Matcher>singletonList(exact);
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         assertFalse(matcher.matches(actual));
     }

     // ---------- hasSimilarMethod ----------

     @Test
     public void hasSimilarMethod_sameMethodUnverifiedSameMock_shouldReturnTrue() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"a"}, false);
         Invocation candidate = createInvocation(MOCK_A, method, new Object[]{"b"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyString());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         assertTrue(matcher.hasSimilarMethod(candidate));
     }

     @Test
     public void hasSimilarMethod_verifiedCandidate_shouldReturnFalse() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"a"}, false);
         // verified == true
         Invocation candidate = createInvocation(MOCK_A, method, new Object[]{"a"}, true);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyString());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         assertFalse(matcher.hasSimilarMethod(candidate));
     }

     @Test
     public void hasSimilarMethod_overloadedButSameArgs_shouldReturnFalse() throws Exception {
         // Two different methods with same name (echo) are not possible on Helper,
         // but we can simulate by using bridge methods where method.equals() is false.
         Method interfaceMethod = GenericInterface.class.getMethod("apply", Object.class);
         Method concreteMethod = Concrete.class.getMethod("apply", String.class);

         Invocation wanted = createInvocation(MOCK_A, interfaceMethod, new Object[]{"x"}, false);
         Invocation candidate = createInvocation(MOCK_A, concreteMethod, new Object[]{"x"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyObject());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);

         // Same name, same mock, unverified, but methods are not .equals() and args match → not
similar
         assertFalse(matcher.hasSimilarMethod(candidate));
     }

     // ---------- safelyArgumentsMatch (via reflection) ----------

     @Test
     public void safelyArgumentsMatch_whenArgumentsMatch_shouldReturnTrue() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"a"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyString());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         boolean result = invokeSafelyArgumentsMatch(matcher, new Object[]{"any"});
         assertTrue(result);
     }

     @Test
     public void safelyArgumentsMatch_whenArgumentsMismatch_shouldReturnFalse() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"expected"}, false);
         Matcher<String> exact = new BaseMatcher<String>() {
             public boolean matches(Object item) { return "expected".equals(item); }
             public void describeTo(Description description) { }
         };
         List<Matcher> matchers = Collections.<Matcher>singletonList(exact);
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         boolean result = invokeSafelyArgumentsMatch(matcher, new Object[]{"different"});
         assertFalse(result);
     }

     @Test
     public void safelyArgumentsMatch_whenComparatorThrows_shouldReturnFalse() throws Exception {
         Method method = Helper.class.getMethod("echo", String.class);
         Invocation wanted = createInvocation(MOCK_A, method, new Object[]{"a"}, false);
         Matcher<String> throwing = new BaseMatcher<String>() {
             public boolean matches(Object item) { throw new RuntimeException("intentional"); }
             public void describeTo(Description description) { }
         };
         List<Matcher> matchers = Collections.<Matcher>singletonList(throwing);
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);
         boolean result = invokeSafelyArgumentsMatch(matcher, new Object[]{"x"});
         assertFalse(result);
     }

     // ---------- hasSameMethod ----------

     @Test
     public void hasSameMethod_bridgeMethod_shouldReturnTrue() throws Exception {
         Method interfaceMethod = GenericInterface.class.getMethod("apply", Object.class);
         Method concreteMethod = Concrete.class.getMethod("apply", String.class);

         Invocation wanted = createInvocation(MOCK_A, interfaceMethod, new Object[]{"x"}, false);
         Invocation candidate = createInvocation(MOCK_A, concreteMethod, new Object[]{"x"}, false);
         List<Matcher> matchers = Collections.<Matcher>singletonList(Matchers.anyObject());
         InvocationMatcher matcher = new InvocationMatcher(wanted, matchers);

         // Bug assertion: bridge methods should be treated as the same method
         assertTrue(matcher.hasSameMethod(candidate));
     }
 }
