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

public class ReturnsSmartNullsTest {

 private ReturnsSmartNulls returnsSmartNulls;

 @Before
 public void setUp() {
     returnsSmartNulls = new ReturnsSmartNulls();
 }

 @Test
 public void shouldPrintTheParametersOnSmartNullPointerExceptionMessage() throws Throwable {
     InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
     Method mockMethod = Object.class.getMethod("toString");
     Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
     Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"oompa", "lumpa"});
     Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Comparable.class);

     ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();

     try {
         Object result = smartNulls.answer(invocation);
         assertNotNull("SmartNull proxy should not be null", result);
         result.toString();
         fail("Expected SmartNullPointerException was not thrown");
     } catch (SmartNullPointerException e) {
         String message = e.getMessage();
         assertNotNull("Exception message should not be null", message);
         assertTrue("Exception message should include 'oompa', but was: " + message,
                 message.contains("oompa"));
         assertTrue("Exception message should include 'lumpa', but was: " + message,
                 message.contains("lumpa"));
     }
 }

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

 @Test
 public void shouldReturnDelegateValueWhenNonNull() throws Throwable {
     InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
     Method mockMethod = Object.class.getMethod("toString");
     Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
     Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) String.class);

     Object result = returnsSmartNulls.answer(invocation);
     assertNotNull("Result should not be null when delegate returns non-null", result);
     assertEquals("Should return delegate value", "", result);
 }

 @Test
 public void shouldReturnNullForNonMockableType() throws Throwable {
     InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
     Method mockMethod = Object.class.getMethod("toString");
     Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
     Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) String.class);

     ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
     Object result = smartNulls.answer(invocation);
     assertNull("Should return null for non-mockable type", result);
 }

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
         assertTrue("Exception message should not be empty", message.length() > 0);
         assertTrue("Exception message should contain argument value 'x', but was: " + message,
                 message.contains("x"));
     }
 }

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

 @Test
 public void shouldIncludeLongStringArguments() throws Throwable {
     InvocationOnMock invocation2 = Mockito.mock(InvocationOnMock.class);
     Method mockMethod2 = Comparable.class.getMethod("compareTo", Object.class);
     Mockito.when(invocation2.getMethod()).thenReturn(mockMethod2);
     String longString = "this is a very long string argument that should appear in the exception
message";
     Mockito.when(invocation2.getArguments()).thenReturn(new Object[]{longString});
     Mockito.when(invocation2.getMethod().getReturnType()).thenReturn((Class) Integer.class);

     ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
     Object smartNull = smartNulls.answer(invocation2);

     String toStringResult = smartNull.toString();
     assertTrue("SmartNull toString() should contain the full long string argument, but was: " +
toStringResult,
             toStringResult.contains(longString));
 }

 @Test
 public void shouldFormatPrimitiveArgumentsCorrectly() throws Throwable {
     InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
     Method mockMethod = Comparable.class.getMethod("compareTo", Object.class);
     Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
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

 @Test
 public void shouldUseDelegateForNonSmartNullTypes() throws Throwable {
     InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
     Method mockMethod = Object.class.getMethod("toString");
     Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
     Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) StringBuilder.class);

     Object result = returnsSmartNulls.answer(invocation);
     assertNotNull("Should return SmartNull proxy for mockable type", result);
     assertFalse("Should not be null", result == null);
 }

 @Test
 public void shouldFormatMethodCallWithCommaSeparatedArgs() throws Throwable {
     InvocationOnMock invocation = Mockito.mock(InvocationOnMock.class);
     Method mockMethod = Comparable.class.getMethod("compareTo", Object.class);
     Mockito.when(invocation.getMethod()).thenReturn(mockMethod);
     Mockito.when(invocation.getArguments()).thenReturn(new Object[]{"first", "second", "third"});
     Mockito.when(invocation.getMethod().getReturnType()).thenReturn((Class) Integer.class);

     ReturnsSmartNulls smartNulls = createSmartNullsWithNullDelegate();
     Object smartNull = smartNulls.answer(invocation);

     String toStringResult = smartNull.toString();
     assertTrue("SmartNull toString() should contain all three arguments, but was: " +
toStringResult,
             toStringResult.contains("first") && toStringResult.contains("second")
                     && toStringResult.contains("third"));
 }

 private ReturnsSmartNulls createSmartNullsWithNullDelegate() {
     return new ReturnsSmartNulls() {
         private static final long serialVersionUID = 1L;

         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable {
             Class<?> type = invocation.getMethod().getReturnType();
             if (ClassImposterizer.INSTANCE.canImposterise(type)) {
                 return ClassImposterizer.INSTANCE.imposterise(
                         new ThrowingInterceptorAccessor(invocation), type);
             }
             return null;
         }
     };
 }

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
         return invocation.getMethod().getName() + "(" + args.substring(1, args.length() - 1) + ")";
     }
 }

}