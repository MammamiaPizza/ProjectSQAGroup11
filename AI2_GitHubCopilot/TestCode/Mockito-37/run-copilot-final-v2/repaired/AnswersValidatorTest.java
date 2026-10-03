package org.mockito.internal.stubbing.answers;

 import static org.mockito.Mockito.*;

 import java.lang.reflect.Method;

 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.exceptions.base.MockitoException;
 import org.mockito.internal.invocation.Invocation;
 import org.mockito.stubbing.Answer;

 public class AnswersValidatorTest {

     private AnswersValidator validator;

     @Before
     public void setUp() {
         validator = new AnswersValidator();
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenStubbingVoidMethodWithReturnValue() {
         Invocation inv = voidInvocation();
         validator.validate(new Returns("value"), inv);
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenReturnValueIsNullForPrimitive() {
         Invocation inv = primitiveReturnInvocation();
         validator.validate(new Returns(null), inv);
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenReturnValueTypeDoesNotMatch() {
         Invocation inv = nonVoidInvocationReturning(String.class, false);
         validator.validate(new Returns(Integer.valueOf(1)), inv);
     }

     @Test
     public void shouldPassWhenReturnValueTypeMatches() {
         Invocation inv = nonVoidInvocationReturning(String.class, true);
         validator.validate(new Returns("match"), inv);
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenNullThrowable() {
         validator.validate(new ThrowsException(null), mock(Invocation.class));
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenCheckedExceptionNotDeclared() {
         Invocation inv = invocationWithInvalidException();
         validator.validate(new ThrowsException(new Exception()), inv);
     }

     @Test
     public void shouldPassWhenThrowableIsRuntimeException() {
         validator.validate(new ThrowsException(new RuntimeException()), mock(Invocation.class));
     }

     @Test
     public void shouldPassWhenThrowableIsError() {
         validator.validate(new ThrowsException(new Error()), mock(Invocation.class));
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenDoesNothingOnNonVoidMethod() {
         Invocation inv = nonVoidInvocation();
         validator.validate(new DoesNothing(), inv);
     }

     @Test
     public void shouldPassWhenDoesNothingOnVoidMethod() {
         validator.validate(new DoesNothing(), voidInvocation());
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenCallingRealMethodOnInterface() {
         validator.validate(new CallsRealMethods(), interfaceMethodInvocation());
     }

     @Test
     public void shouldPassWhenCallingRealMethodOnClass() {
         validator.validate(new CallsRealMethods(), classMethodInvocation());
     }

     @Test(expected = MockitoException.class)
     public void shouldFailWhenCallingRealMethodOnVoidInterface() {
         Invocation inv = interfaceMethodInvocation();
         when(inv.isVoid()).thenReturn(true);
         validator.validate(new CallsRealMethods(), inv);
     }

     private Invocation voidInvocation() {
         Invocation inv = mock(Invocation.class);
         when(inv.isVoid()).thenReturn(true);
         return inv;
     }

     private Invocation nonVoidInvocation() {
         Invocation inv = mock(Invocation.class);
         when(inv.isVoid()).thenReturn(false);
         return inv;
     }

     private Invocation primitiveReturnInvocation() {
         Invocation inv = mock(Invocation.class);
         when(inv.returnsPrimitive()).thenReturn(true);
         when(inv.isVoid()).thenReturn(false);
         when(inv.printMethodReturnType()).thenReturn("int");
         when(inv.getMethodName()).thenReturn("myMethod");
         return inv;
     }

     private Invocation nonVoidInvocationReturning(Class<?> returnType, boolean validReturn) {
         Invocation inv = mock(Invocation.class);
         when(inv.isVoid()).thenReturn(false);
         when(inv.returnsPrimitive()).thenReturn(false);
         when(inv.printMethodReturnType()).thenReturn(returnType.getSimpleName());
         when(inv.getMethodName()).thenReturn("myMethod");
         when(inv.isValidReturnType(any())).thenReturn(validReturn);
         return inv;
     }

     private Invocation invocationWithInvalidException() {
         Invocation inv = mock(Invocation.class);
         when(inv.isValidException(any())).thenReturn(false);
         return inv;
     }

     private Invocation interfaceMethodInvocation() {
         return invocationWithDeclaringClass(AnInterface.class);
     }

     private Invocation classMethodInvocation() {
         return invocationWithDeclaringClass(Object.class);
     }

     private Invocation invocationWithDeclaringClass(Class<?> declaringClass) {
         Invocation inv = mock(Invocation.class);
         Method method = mock(Method.class);
         doReturn(declaringClass).when(method).getDeclaringClass();
         when(inv.getMethod()).thenReturn(method);
         return inv;
     }

     private interface AnInterface {
         String annotated();
     }
 }
