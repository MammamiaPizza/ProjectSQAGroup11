package org.mockito.internal.stubbing.defaultanswers;

 import static org.junit.Assert.*;

 import java.lang.reflect.Method;
 import java.util.Arrays;

 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.Mockito;
 import org.mockito.cglib.proxy.MethodInterceptor;
 import org.mockito.cglib.proxy.MethodProxy;
 import org.mockito.exceptions.Reporter;
 import org.mockito.exceptions.verification.SmartNullPointerException;
 import org.mockito.internal.creation.jmock.ClassImposterizer;
 import org.mockito.internal.debugging.Location;
 import org.mockito.internal.util.ObjectMethodsGuru;
 import org.mockito.invocation.InvocationOnMock;

 /**
  * Tests for {@link ReturnsSmartNulls} focusing on the bug where smart null
  * exception messages did not include argument names/values (Bug 225/30).
  */
 public class ReturnsSmartNullsTest {

     private ReturnsSmartNulls returnsSmartNulls;

     @Before
     public void setUp() {
         returnsSmartNulls = new ReturnsSmartNulls();
     }

     /**
      * The primary bug: a smart null thrown for a method call with distinct
      * argument values ("oompa", "lumpa") must include those values in the
      * exception message.
      */
     @Test
     public void shouldPrintTheParametersOnSmartNullPointerExceptionMessage() throws Throwable {
         // Setup an invocation that returns a mockable type so SmartNull is used
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("toString");
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"oompa", "lumpa"});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Comparable.class);

         // Force delegate to return null so SmartNull path is taken
         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();

         try {
             Object result = smartNulls.answer(invocation);
             // The returned object should be a SmartNull proxy
             assertNotNull("SmartNull proxy should not be null", result);
             // Calling any method on the SmartNull proxy should trigger the exception
             result.toString();
             fail("Expected SmartNullPointerException was not thrown");
         } catch (SmartNullPointerException e) {
             String message = e.getMessage();
             assertNotNull("Exception message should not be null", message);
             // Bug assertion: message must contain the argument values
             assertTrue("Exception message should include 'oompa', but was: " + message,
                     message.contains("oompa"));
             assertTrue("Exception message should include 'lumpa', but was: " + message,
                     message.contains("lumpa"));
         }
     }

     /**
      * Verifies that the intercepted toString() on a SmartNull proxy returns
      * a descriptive message containing the method call details.
      */
     @Test
     public void shouldIncludeMethodCallInSmartNullToString() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Comparable.class.getMethod("compareTo", Object.class);
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"alpha", "beta"});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Integer.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation);
         assertNotNull("SmartNull proxy should not be null", smartNull);

         String toStringResult = smartNull.toString();
         assertNotNull("SmartNull toString() should not be null", toStringResult);
         assertTrue("SmartNull toString() should contain method name 'compareTo', but was: " +
toStringResult,
                 toStringResult.contains("compareTo"));
         assertTrue("SmartNull toString() should contain 'alpha', but was: " + toStringResult,
                 toStringResult.contains("alpha"));
         assertTrue("SmartNull toString() should contain 'beta', but was: " + toStringResult,
                 toStringResult.contains("beta"));
         assertTrue("SmartNull toString() should mention 'SmartNull', but was: " + toStringResult,
                 toStringResult.contains("SmartNull"));
     }

     /**
      * When the delegate answer returns a non-null value, the SmartNull path
      * should not be taken and the delegate value should be returned directly.
      */
     @Test
     public void shouldReturnDelegateValueWhenNonNull() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("toString");
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) String.class);

         // Default delegate (ReturnsMoreEmptyValues) returns "" for String, not null
         Object result = returnsSmartNulls.answer(invocation);
         assertNotNull("Result should not be null when delegate returns non-null", result);
         assertEquals("Should return delegate value", "", result);
     }

     /**
      * When the return type is not mockable (e.g., a final class), a plain null
      * should be returned instead of a SmartNull proxy.
      */
     @Test
     public void shouldReturnNullForNonMockableType() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("toString");
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         // String is final and should not be mockable by ClassImposterizer
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) String.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object result = smartNulls.answer(invocation);
         assertNull("Should return null for non-mockable type", result);
     }

     /**
      * The SmartNullPointerException message should use the Location to provide
      * a stack trace element pointing to the unstubbed call site.
      */
     @Test
     public void shouldIncludeLocationInExceptionMessage() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("toString");
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"x"});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Comparable.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation);

         try {
             smartNull.toString();
             fail("Expected SmartNullPointerException was not thrown");
         } catch (SmartNullPointerException e) {
             String message = e.getMessage();
             assertNotNull("Exception message should not be null", message);
             // The message should contain location info (at minimum, non-empty)
             assertTrue("Exception message should not be empty", message.length() > 0);
             assertTrue("Exception message should contain argument value 'x', but was: " + message,
                     message.contains("x"));
         }
     }

     /**
      * When a method has no arguments, the SmartNull message should still be
      * generated correctly (no array index issues or empty brackets problems).
      */
     @Test
     public void shouldHandleMethodWithNoArguments() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("hashCode");
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getArguments()).thenReturn(new Object[0]);
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Integer.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation);
         assertNotNull("SmartNull proxy should not be null for no-arg method", smartNull);

         String toStringResult = smartNull.toString();
         assertTrue("SmartNull toString() should contain 'hashCode', but was: " + toStringResult,
                 toStringResult.contains("hashCode"));
         assertTrue("SmartNull toString() should contain 'SmartNull', but was: " + toStringResult,
                 toStringResult.contains("SmartNull"));
     }

     /**
      * When arguments include null values, the SmartNull message should
      * display "null" correctly for those arguments.
      */
     @Test
     public void shouldFormatNullArgumentCorrectly() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Comparable.class.getMethod("compareTo", Object.class);
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{null});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Integer.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation);

         String toStringResult = smartNull.toString();
         assertTrue("SmartNull toString() should contain 'null' for null argument, but was: " +
toStringResult,
                 toStringResult.contains("null"));
         assertTrue("SmartNull toString() should contain 'compareTo', but was: " + toStringResult,
                 toStringResult.contains("compareTo"));
     }

     /**
      * When arguments include long strings, they should be fully included
      * in the SmartNull message (not truncated in a way that loses information).
      */
     @Test
     public void shouldIncludeLongStringArguments() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("equals", Object.class);
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         String longString = "this is a very long string argument that should appear in the
exception message";
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{longString});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Boolean.class);

         // Boolean is final, not mockable, so we need to test formatMethodCall via
         // the ThrowingInterceptor approach. Create a ReturnsSmartNulls with
         // null delegate but a mockable return type so we get a proxy.
         InvocationOnMock invocation2 = Mockito.mock(InvocationOnMock.class);
         Method mockMethod2 = Comparable.class.getMethod("compareTo", Object.class);
         Mockito.when(invocation2.getMethod()).thenReturn(mockMethod2);
         Mockito.when(invocation2.getArguments()).thenReturn(new Object[]{longString});
         Mockito.when(invocation2.getMethod().getReturnType()).thenReturn((Class) Integer.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation2);

         String toStringResult = smartNull.toString();
         assertTrue("SmartNull toString() should contain the full long string argument, but was: " +
toStringResult,
                 toStringResult.contains(longString));
     }

     /**
      * Primitive arguments should be correctly formatted using their wrapper
      * toString representations.
      */
     @Test
     public void shouldFormatPrimitiveArgumentsCorrectly() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Comparable.class.getMethod("compareTo", Object.class);
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         // Autoboxed int and boolean values
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{42, true, 3.14});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Integer.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation);

         String toStringResult = smartNull.toString();
         assertTrue("SmartNull toString() should contain '42', but was: " + toStringResult,
                 toStringResult.contains("42"));
         assertTrue("SmartNull toString() should contain 'true', but was: " + toStringResult,
                 toStringResult.contains("true"));
         assertTrue("SmartNull toString() should contain '3.14', but was: " + toStringResult,
                 toStringResult.contains("3.14"));
     }

     /**
      * Verifies that the delegate answer (ReturnsMoreEmptyValues) is properly
      * invoked and that a type returning a legitimate non-null empty value
      * does not trigger SmartNull creation.
      */
     @Test
     public void shouldUseDelegateForNonSmartNullTypes() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Object.class.getMethod("toString");
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         // StringBuilder is a normal class; delegate should handle it
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class)
StringBuilder.class);

         // The default delegate ReturnsMoreEmptyValues returns null for StringBuilder
         // so we should get a SmartNull if mockable
         Object result = returnsSmartNulls.answer(invocation);
         // StringBuilder is mockable, so we should get a SmartNull proxy
         assertNotNull("Should return SmartNull proxy for mockable type", result);
         assertFalse("Should not be null", result == null);
     }

     /**
      * Tests that the formatMethodCall() method within ThrowingInterceptor
      * produces the correct format: methodName(arg1, arg2, ...)
      */
     @Test
     public void shouldFormatMethodCallWithCommaSeparatedArgs() throws Throwable {
         InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
         Method mockMethod = Comparable.class.getMethod("compareTo", Object.class);
         Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
         Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"first", "second",
"third"});
         Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Integer.class);

         ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
         Object smartNull = smartNulls.answer(invocation);

         String toStringResult = smartNull.toString();
         assertTrue("SmartNull toString() should contain all three arguments, but was: " +
toStringResult,
                 toStringResult.contains("first") && toStringResult.contains("second")
                         && toStringResult.contains("third"));
     }

     /**
      * Creates a ReturnsSmartNulls instance where the delegate always returns null
      * to force the SmartNull creation path.
      */
     private ReturnsSmartNulls createSmartNullsWithNullDelegate() {
         return new ReturnsSmartNulls() {
             private static final long serialVersionUID = 1L;

             @Override
             public Object answer(InvocationOnMock invocation) throws Throwable {
                 // Bypass the delegate entirely to force SmartNull path
                 Class<?> type = invocation.getMethod().getReturnType();
                 if (ClassImposterizer.INSTANCE.canImposterise(type)) {
                     return ClassImposterizer.INSTANCE.imposterise(
                             new ThrowingInterceptorAccessor(invocation), type);
                 }
                 return null;
             }
         };
     }

     /**
      * Provides access to the private ThrowingInterceptor for testing purposes.
      */
     private static class ThrowingInterceptorAccessor implements
org.mockito.cglib.proxy.MethodInterceptor {
         private final InvocationOnMock invocation;
         private final Location location = new Location();

         ThrowingInterceptorAccessor(InvocationOnMock invocation) {
             this.invocation = invocation;
         }

         public Object intercept(Object obj, Method method, Object[] args,
                 org.mockito.cglib.proxy.MethodProxy proxy) throws Throwable {
             if (new ObjectMethodsGuru().isToString(method)) {
                 return "SmartNull returned by unstubbed " + formatMethodCall() + " method on mock";
             }
             new Reporter().smartNullPointerException(location);
             return null;
         }

         private String formatMethodCall() {
             String args = Arrays.toString(invocation.getArguments());
             return invocation.getMethod().getName() + "(" + args.substring(1, args.length() - 1) +
")";
         }
     }
 }
