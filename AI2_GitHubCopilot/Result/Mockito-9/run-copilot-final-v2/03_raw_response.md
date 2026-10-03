package org.mockito.internal.stubbing.answers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

public class CallsRealMethodsTest {

 static abstract class AbstractClass {
     abstract String getString();
     abstract int getInt();
     abstract boolean getBoolean();
     abstract long getLong();
     abstract void doSomething();
     String concreteMethod() {
         return "real";
     }
     void sideEffectMethod(StringBuilder sb) {
         sb.append("modified");
     }
     void throwingMethod() {
         throw new RuntimeException("boom");
     }
 }

 interface DefaultInterface {
     String greet();
 }

 @Test
 public void shouldReturnNullForAbstractObjectMethod() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     assertNull(mock.getString());
 }

 @Test
 public void shouldReturnZeroForAbstractIntMethod() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     assertEquals(0, mock.getInt());
 }

 @Test
 public void shouldReturnFalseForAbstractBooleanMethod() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     assertFalse(mock.getBoolean());
 }

 @Test
 public void shouldReturnZeroForAbstractLongMethod() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     assertEquals(0L, mock.getLong());
 }

 @Test
 public void shouldInvokeAbstractVoidMethodWithoutException() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     mock.doSomething(); // should not throw
 }

 @Test
 public void shouldInvokeConcreteMethodAndReturnRealResult() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     assertEquals("real", mock.concreteMethod());
 }

 @Test
 public void shouldReturnDefaultForInterfaceMethod() {
     DefaultInterface mock = mock(DefaultInterface.class, new CallsRealMethods());
     assertNull(mock.greet());
 }

 @Test
 public void shouldPropagateExceptionFromRealMethod() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     try {
         mock.throwingMethod();
         fail("expected RuntimeException");
     } catch (RuntimeException e) {
         assertEquals("boom", e.getMessage());
     }
 }

 @Test
 public void shouldHaveSideEffectFromConcreteMethod() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     StringBuilder sb = new StringBuilder();
     mock.sideEffectMethod(sb);
     assertEquals("modified", sb.toString());
 }

 @Test(expected = NullPointerException.class)
 public void shouldThrowNullPointerExceptionWhenInvocationIsNull() throws Throwable {
     new CallsRealMethods().answer(null);
 }

 @Test
 public void shouldReturnDefaultForAbstractMethodOnMultipleCalls() {
     AbstractClass mock = mock(AbstractClass.class, new CallsRealMethods());
     assertNull(mock.getString());
     assertNull(mock.getString());
 }

}