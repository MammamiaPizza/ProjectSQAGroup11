package org.mockito.internal.invocation;

 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.lang.reflect.Method;

 import org.junit.Test;
 import org.mockito.internal.invocation.realmethod.RealMethod;
 import org.mockito.internal.util.MockUtil;

 /**
  * Tests for {@link Invocation#callRealMethod()} focusing on the bug where
  * calling a real method on an interface mock produced a cryptic
  * {@link NullPointerException} or {@link NoSuchMethodError} instead of a
  * meaningful exception.
  */
 public class InvocationTest {

     /** Plain interface used to create interface mocks / spies. */
     interface Foo {
         String bar();
         int returnPrimitive();
         void voidMethod();
         String varArgsMethod(String first, String... rest);
     }

     // -------------------------------------------------------------------
     // 1. Core bug: interface mock – must scream, not NPE / NSME
     // -------------------------------------------------------------------

     @Test(expected = RuntimeException.class)
     public void shouldScreamWhenCallingRealMethodOnInterface() throws Throwable {
         Invocation invocation = createInvocationOnInterfaceMock("bar");
         invocation.callRealMethod();
     }

     @Test
     public void shouldGiveMeaningfulMessageWhenCallingRealMethodOnInterface() throws Throwable {
         Invocation invocation = createInvocationOnInterfaceMock("voidMethod");
         try {
             invocation.callRealMethod();
             fail("RuntimeException expected");
         } catch (RuntimeException e) {
             String msg = e.getMessage().toLowerCase();
             assertTrue("message must mention 'real method'", msg.contains("real method"));
             assertTrue("message must mention 'interface'", msg.contains("interface"));
         }
     }

     @Test
     public void shouldScreamWhenCallingRealMethodOnInterfaceSpy() throws Throwable {
         // spies on interfaces also use a proxy that only implements the interface
         Foo spied = mock(Foo.class, withSettings().spiedInstance(new Foo() {
             public String bar() { return null; }
             public int returnPrimitive() { return 0; }
             public void voidMethod() {}
             public String varArgsMethod(String first, String... rest) { return null; }
         }));
         // Extract the invocation the same way – the mock object is the proxy.
         Invocation invocation = buildInvocation(spied, "bar");
         try {
             invocation.callRealMethod();
             fail("expected RuntimeException");
         } catch (RuntimeException e) {
             assertTrue(e.getMessage().toLowerCase().contains("interface"));
         }
     }

     // -------------------------------------------------------------------
     // 2. Normal concrete mock – real method is invoked correctly
     // -------------------------------------------------------------------

     @Test
     public void shouldInvokeRealMethodOnConcreteMock() throws Throwable {
         Object concreteMock = new Object(); // concrete class
         RealMethod realMethod = mock(RealMethod.class);
         when(realMethod.invoke(concreteMock, new Object[0])).thenReturn("result");

         Invocation invocation = new Invocation(concreteMock,
                 aMethodFor("toString"), new Object[0], 1, realMethod);
         assertEquals("result", invocation.callRealMethod());
     }

     @Test
     public void shouldPassRawArgumentsToRealMethod() throws Throwable {
         Object concreteMock = new Object();
         Object[] rawArgs = new Object[] { "a", "b" };
         RealMethod realMethod = mock(RealMethod.class);
         when(realMethod.invoke(concreteMock, rawArgs)).thenReturn("ok");

         Invocation invocation = new Invocation(concreteMock,
                 aMethodFor("equals"), rawArgs, 1, realMethod);
         assertEquals("ok", invocation.callRealMethod());
         verify(realMethod).invoke(concreteMock, rawArgs);
     }

     @Test
     public void shouldHandlePrimitiveReturnType() throws Throwable {
         Object concreteMock = new Object();
         RealMethod realMethod = mock(RealMethod.class);
         when(realMethod.invoke(concreteMock, new Object[0])).thenReturn(Integer.valueOf(42));

         MockitoMethod method = methodReturning(int.class);
         Invocation invocation = new Invocation(concreteMock, method, new Object[0], 1,
                 realMethod);
         assertEquals(42, invocation.callRealMethod());
     }

     @Test
     public void shouldPropagateCheckedExceptionFromRealMethod() throws Throwable {
         Object concreteMock = new Object();
         Exception checked = new Exception("checked");

         RealMethod realMethod = mock(RealMethod.class);
         when(realMethod.invoke(concreteMock, new Object[0])).thenThrow(checked);

         Invocation invocation = new Invocation(concreteMock,
                 aMethodFor("hashCode"), new Object[0], 1, realMethod);
         try {
             invocation.callRealMethod();
             fail("expected Exception");
         } catch (Exception e) {
             assertSame(checked, e);
         }
     }

     // -------------------------------------------------------------------
     // 3. Edge cases: varargs, zero arguments, null RealMethod
     // -------------------------------------------------------------------

     @Test
     public void shouldPreserveAndUseRawArgumentsWithVarArgs() throws Throwable {
         Object concreteMock = new Object();
         // compressed varargs: ("first", ["rest1", "rest2"])
         Object[] compressed = new Object[] { "first", new String[] { "rest1", "rest2" } };

         RealMethod realMethod = mock(RealMethod.class);
         when(realMethod.invoke(eq(concreteMock), eq(compressed))).thenReturn("done");

         MockitoMethod method = methodIsVarArgs("varArgsMethod");
         Invocation invocation = new Invocation(concreteMock, method, compressed, 1,
                 realMethod);
         assertEquals("done", invocation.callRealMethod());
         // rawArguments untouched
         assertArrayEquals(compressed, invocation.getRawArguments());
     }

     @Test
     public void shouldWorkWithZeroArguments() throws Throwable {
         Object concreteMock = new Object();
         RealMethod realMethod = mock(RealMethod.class);
         when(realMethod.invoke(concreteMock, new Object[0])).thenReturn("zero");

         Invocation invocation = new Invocation(concreteMock,
                 aMethodFor("toString"), new Object[0], 1, realMethod);
         assertEquals("zero", invocation.callRealMethod());
     }

     @Test
     public void shouldThrowNullPointerWhenRealMethodIsNull() {
         Object concreteMock = new Object();
         Invocation invocation = new Invocation(concreteMock,
                 aMethodFor("toString"), new Object[0], 1, null);
         try {
             invocation.callRealMethod();
             fail("expected NullPointerException");
         } catch (NullPointerException expected) {
             // realMethod is null, so invoke() NPEs – fine here
         } catch (Throwable t) {
             fail("unexpected exception type: " + t.getClass().getSimpleName());
         }
     }

     // -------------------------------------------------------------------
     // 4. Interface plus Object-methods (toString, etc.)
     // -------------------------------------------------------------------

     @Test
     public void shouldScreamWhenCallingToStringOnInterfaceMock() throws Throwable {
         Invocation invocation = createInvocationOnInterfaceMock("toString");
         try {
             invocation.callRealMethod();
             fail("expected RuntimeException");
         } catch (RuntimeException e) {
             assertTrue(e.getMessage().toLowerCase().contains("interface"));
         }
     }

     // -------------------------------------------------------------------
     // helpers
     // -------------------------------------------------------------------

     private Invocation createInvocationOnInterfaceMock(String methodName) throws Exception {
         Foo mock = mock(Foo.class);
         return buildInvocation(mock, methodName);
     }

     private Invocation buildInvocation(Object interfaceMock, String methodName)
             throws Exception {
         Method javaMethod = Foo.class.getMethod(methodName);
         MockitoMethod mockitoMethod = mock(MockitoMethod.class);
         when(mockitoMethod.getJavaMethod()).thenReturn(javaMethod);
         when(mockitoMethod.getName()).thenReturn(methodName);
         when(mockitoMethod.getReturnType()).thenReturn(javaMethod.getReturnType());
         when(mockitoMethod.isVarArgs()).thenReturn(javaMethod.isVarArgs());

         RealMethod realMethod = mock(RealMethod.class); // non-null to avoid immediate NPE

         Object[] args = buildDefaultArgs(javaMethod);
         return new Invocation(interfaceMock, mockitoMethod, args, 1, realMethod);
     }

     private Object[] buildDefaultArgs(Method m) {
         if (m.getParameterTypes().length == 0)
             return new Object[0];
         if (m.isVarArgs()) {
             // provide a single-element compressed varargs to test raw preservation
             return new Object[] { new String[] { "v" } };
         }
         return new Object[] { null }; // fallback
     }

     private static MockitoMethod aMethodFor(String name) {
         MockitoMethod m = mock(MockitoMethod.class);
         when(m.getName()).thenReturn(name);
         when(m.getJavaMethod()).thenReturn(Object.class.getMethods()[0]); // any real method
         when(m.isVarArgs()).thenReturn(false);
         when(m.getReturnType()).thenReturn(Object.class);
         return m;
     }

     private static MockitoMethod methodReturning(Class<?> returnType) {
         MockitoMethod m = mock(MockitoMethod.class);
         when(m.getName()).thenReturn("dummy");
         when(m.getJavaMethod()).thenReturn(Object.class.getMethods()[0]);
         when(m.isVarArgs()).thenReturn(false);
         when(m.getReturnType()).thenReturn(returnType);
         return m;
     }

     private static MockitoMethod methodIsVarArgs(String name) {
         MockitoMethod m = mock(MockitoMethod.class);
         when(m.getName()).thenReturn(name);
         when(m.getJavaMethod()).thenReturn(Foo.class.getMethods()[0]);
         when(m.isVarArgs()).thenReturn(true);
         when(m.getReturnType()).thenReturn(Object.class);
         return m;
     }
 }
