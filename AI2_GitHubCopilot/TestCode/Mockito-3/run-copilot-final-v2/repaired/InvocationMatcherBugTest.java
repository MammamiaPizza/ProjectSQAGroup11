package org.mockito.internal.invocation;

 import static org.junit.Assert.*;
 import static org.junit.Assume.*;

 import java.lang.reflect.Array;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;

 import org.hamcrest.Matcher;
 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.internal.matchers.CapturesArguments;
 import org.mockito.invocation.Invocation;
 import org.mockito.invocation.Location;
 import org.mockito.internal.matchers.Equals;

 /**
  * Tests for {@link InvocationMatcher} that expose buggy behaviour with varargs
  * (issue #188). The tests are written against the expected contract.
  */
 public class InvocationMatcherBugTest {

     // Test interfaces to obtain vararg Method objects
     private interface VarArgMethods {
         void mixed(String s, int... nums);
         void pureVararg(int... nums);
         void byteVararg(byte... b);
         void nonVararg(String s, int i);
     }

     private Method mixedMethod;
     private Method pureVarargMethod;
     private Method byteVarargMethod;
     private Method nonVarargMethod;

     @Before
     public void setUp() throws Exception {
         mixedMethod = VarArgMethods.class.getDeclaredMethod("mixed", String.class, int[].class);
         pureVarargMethod = VarArgMethods.class.getDeclaredMethod("pureVararg", int[].class);
         byteVarargMethod = VarArgMethods.class.getDeclaredMethod("byteVararg", byte[].class);
         nonVarargMethod = VarArgMethods.class.getDeclaredMethod("nonVararg", String.class,
int.class);
     }

     // --------------------------------------------------------------------------------
     // matches() tests – varargs matching element-by-element, no AIOBE
     // --------------------------------------------------------------------------------

     @Test
     public void matches_vararg_elementsEqual_shouldMatch() {
         // wanted: mixed("x", 1, 2)
         Invocation wanted = invocation(mixedMethod, "x", new int[]{1, 2});
         List<Matcher> matchers = Arrays.<Matcher>asList(
                 new Equals("x"), new Equals(1), new Equals(2));
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         // actual: mixed("x", 1, 2) – varargs expanded as individual ints
         Invocation actual = invocation(mixedMethod, "x", 1, 2);
         assertTrue("Vararg arguments should match element by element",
                 im.matches(actual));
     }

     @Test
     public void matches_vararg_actualFewerExpandedArgs_shouldNotThrowAndReturnFalse() {
         // wanted: pureVararg(1, 2, 3) – three matchers
         Invocation wanted = invocation(pureVarargMethod, new int[]{1, 2, 3});
         List<Matcher> matchers = Arrays.<Matcher>asList(
                 new Equals(1), new Equals(2), new Equals(3));
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         // actual: pureVararg(1) – only one expanded element (raw array length 1)
         Invocation actual = invocation(pureVarargMethod, new int[]{1});
         // Bug would throw ArrayIndexOutOfBoundsException inside ArgumentsComparator
         assertFalse("Should not throw AIOBE when actual varargs are fewer",
                 im.matches(actual));
     }

     @Test
     public void matches_vararg_actualMoreExpandedArgs_shouldNotThrowAndReturnFalse() {
         Invocation wanted = invocation(pureVarargMethod, new int[]{1});
         List<Matcher> matchers = Collections.<Matcher>singletonList(new Equals(1));
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         Invocation actual = invocation(pureVarargMethod, new int[]{1, 2, 3});
         // Actual has more elements than matchers – should not explode
         assertFalse("Should return false when actual has extra vararg elements",
                 im.matches(actual));
     }

     // --------------------------------------------------------------------------------
     // captureArgumentsFrom() tests – individual vararg element capture, no AIOBE,
     // primitive byte vararg handling
     // --------------------------------------------------------------------------------

     @Test
     public void captureArgumentsFrom_mixedVararg_capturesEachElementIndividually() {
         ListCaptor captorS = new ListCaptor();
         ListCaptor captorN1 = new ListCaptor();
         ListCaptor captorN2 = new ListCaptor();

         Invocation wanted = invocation(mixedMethod, "a", new int[]{10, 20});
         List<Matcher> matchers = Arrays.<Matcher>asList(captorS, captorN1, captorN2);
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         // actual invocation: mixed("hello", 10, 20)
         Invocation actual = invocation(mixedMethod, "hello", 10, 20);
         im.captureArgumentsFrom(actual);

         assertEquals("Should capture non-vararg string", "hello", captorS.lastCaptured());
         assertEquals("Should capture first vararg element as Integer 10", 10,
                 captorN1.lastCaptured());
         assertEquals("Should capture second vararg element as Integer 20", 20,
                 captorN2.lastCaptured());
     }

     @Test
     public void captureArgumentsFrom_pureVararg_multipleMatchersNoAIOBE() {
         ListCaptor c1 = new ListCaptor();
         ListCaptor c2 = new ListCaptor();

         Invocation wanted = invocation(pureVarargMethod, new int[]{42, 99});
         List<Matcher> matchers = Arrays.<Matcher>asList(c1, c2);
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         // actual: pureVararg(42, 99) – the raw argument is an int[]{42,99}
         Invocation actual = invocation(pureVarargMethod, new int[]{42, 99});
         im.captureArgumentsFrom(actual);

         assertEquals("First captured vararg", 42, c1.lastCaptured());
         assertEquals("Second captured vararg", 99, c2.lastCaptured());
     }

     @Test
     public void captureArgumentsFrom_pureVararg_noActualArgs_capturesNothingFromVararg() {
         ListCaptor c = new ListCaptor();
         Invocation wanted = invocation(pureVarargMethod, new int[0]);
         List<Matcher> matchers = Collections.<Matcher>singletonList(c);
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         // empty vararg call
         Invocation actual = invocation(pureVarargMethod, new int[0]);
         im.captureArgumentsFrom(actual);

         // Nothing captured – but no exception should be thrown
         assertTrue("No value captured when vararg is empty", c.allCaptured().isEmpty());
     }

     @Test
     public void captureArgumentsFrom_byteVararg_capturesByteNotArray() {
         ListCaptor captor = new ListCaptor();

         Invocation wanted = invocation(byteVarargMethod, new byte[]{7});
         List<Matcher> matchers = Collections.<Matcher>singletonList(captor);
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         // actual: byteVararg with a single byte value 7 -> raw arg is byte[]{7}
         Invocation actual = invocation(byteVarargMethod, new byte[]{7});
         im.captureArgumentsFrom(actual);

         Object captured = captor.lastCaptured();
         assertNotNull("Should have captured a value", captured);
         // The buggy code captures the whole byte[] array, not the element.
         assertTrue("Captured value should be a Byte, not byte[" + captured.getClass().getName() +
"]",
                 captured instanceof Byte);
         assertEquals("Captured byte value", (byte) 7, ((Byte) captured).byteValue());
     }

     @Test
     public void captureArgumentsFrom_byteVararg_multipleElements() {
         ListCaptor c1 = new ListCaptor();
         ListCaptor c2 = new ListCaptor();

         Invocation wanted = invocation(byteVarargMethod, new byte[]{1, 2});
         List<Matcher> matchers = Arrays.<Matcher>asList(c1, c2);
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         Invocation actual = invocation(byteVarargMethod, new byte[]{1, 2});
         im.captureArgumentsFrom(actual);

         assertEquals("First byte", (byte) 1, c1.lastCaptured());
         assertEquals("Second byte", (byte) 2, c2.lastCaptured());
     }

     @Test
     public void captureArgumentsFrom_nonVararg_worksNormally() {
         ListCaptor c1 = new ListCaptor();
         ListCaptor c2 = new ListCaptor();

         Invocation wanted = invocation(nonVarargMethod, "text", 5);
         List<Matcher> matchers = Arrays.<Matcher>asList(c1, c2);
         InvocationMatcher im = new InvocationMatcher(wanted, matchers);

         Invocation actual = invocation(nonVarargMethod, "text", 5);
         im.captureArgumentsFrom(actual);

         assertEquals("Non-vararg string", "text", c1.lastCaptured());
         assertEquals("Non-vararg int", 5, c2.lastCaptured());
     }

     // --------------------------------------------------------------------------------
     // Utility: simple Invocation implementation + argument matchers
     // --------------------------------------------------------------------------------

     /** A simple stand-in for an Invocation that respects raw argument layout. */
     static class MockInvocation implements Invocation {
         private final Object mock;
         private final Method method;
         private final Object[] rawArgs;
         private boolean verified = false;

         MockInvocation(Method method, Object... rawArgs) {
             this.mock = new Object(); // dummy
             this.method = method;
             this.rawArgs = rawArgs;
         }

         @Override public Object getMock() { return mock; }
         @Override public Method getMethod() { return method; }

         /**
          * In buggy Mockito, getArguments() may not expand varargs, leading to
          * mismatched lengths. We return the raw arguments here to simulate that.
          */
         @Override public Object[] getArguments() { return rawArgs; }

         @Override public Object[] getRawArguments() { return rawArgs; }

         @Override public Object getArgumentAt(int index, Class<?> clazz) {
             if (index < 0 || index >= rawArgs.length)
                 throw new ArrayIndexOutOfBoundsException(index);
             return rawArgs[index];
         }

         @Override public boolean isVerified() { return verified; }
         @Override public Location getLocation() { return null; }

         // unused methods – return default values
         @Override public Object callRealMethod() throws Throwable { return null; }
         @Override public int getSequenceNumber() { return 0; }
         @Override public void markVerified() { verified = true; }
         @Override public void markStubUsed() { }
         @Override public boolean isIgnoredForVerification() { return false; }
     }

     /** A matcher that stores all captured values for later inspection. */
     static class ListCaptor extends org.hamcrest.BaseMatcher<Object> implements CapturesArguments {
         private final List<Object> captured = new ArrayList<Object>();

         @Override public boolean matches(Object item) { return true; }

         @Override public void describeTo(org.hamcrest.Description description) {
             description.appendText("captor");
         }

         @Override public void captureFrom(Object argument) {
             captured.add(argument);
         }

         Object lastCaptured() {
             if (captured.isEmpty())
                 return null;
             return captured.get(captured.size() - 1);
         }

         List<Object> allCaptured() {
             return captured;
         }
     }

     /** Convenience factory for a MockInvocation with the supplied raw arguments. */
     static Invocation invocation(Method method, Object... rawArgs) {
         return new MockInvocation(method, rawArgs);
     }
 }
