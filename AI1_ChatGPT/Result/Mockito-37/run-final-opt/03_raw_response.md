package org.mockito.internal.stubbing.answers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.invocation.Invocation;

public class AnswersValidatorGeneratedTest {

    public interface AbstractService {
        String value();
    }

    public static class ConcreteService {
        public String value() {
            return "real value";
        }
    }

    @Test(expected = MockitoException.class)
    public void shouldFailFastWhenCallingRealMethodOnAnInterface() {
        AbstractService service = mock(AbstractService.class);

        when(service.value()).thenCallRealMethod();
    }

    @Test
    public void shouldAllowCallingRealMethodOnConcreteClass() {
        ConcreteService service = mock(ConcreteService.class, CALLS_REAL_METHODS);

        assertEquals("real value", service.value());
    }

    @Test
    public void shouldAllowDoNothingForVoidMethod() {
        Invocation invocation = mock(Invocation.class);
        when(invocation.isVoid()).thenReturn(true);

        new AnswersValidator().validate(new DoesNothing(), invocation);
    }

    @Test(expected = MockitoException.class)
    public void shouldRejectDoNothingForNonVoidMethod() {
        Invocation invocation = mock(Invocation.class);
        when(invocation.isVoid()).thenReturn(false);

        new AnswersValidator().validate(new DoesNothing(), invocation);
    }

    @Test
    public void shouldAllowCompatibleReturnValue() {
        Invocation invocation = mock(Invocation.class);
        when(invocation.isVoid()).thenReturn(false);
        when(invocation.returnsPrimitive()).thenReturn(false);
        when(invocation.isValidReturnType(String.class)).thenReturn(true);

        new AnswersValidator().validate(new Returns("value"), invocation);
    }

    @Test(expected = MockitoException.class)
    public void shouldRejectReturnValueForVoidMethod() {
        Invocation invocation = mock(Invocation.class);
        when(invocation.isVoid()).thenReturn(true);

        new AnswersValidator().validate(new Returns("value"), invocation);
    }

    @Test(expected = MockitoException.class)
    public void shouldRejectNullReturnValueForPrimitiveMethod() {
        Invocation invocation = mock(Invocation.class);
        when(invocation.isVoid()).thenReturn(false);
        when(invocation.returnsPrimitive()).thenReturn(true);
        when(invocation.printMethodReturnType()).thenReturn("int");
        when(invocation.getMethodName()).thenReturn("number");

        new AnswersValidator().validate(new Returns(null), invocation);
    }

    @Test(expected = MockitoException.class)
    public void shouldRejectIncompatibleReturnValue() {
        Invocation invocation = mock(Invocation.class);
        when(invocation.isVoid()).thenReturn(false);
        when(invocation.returnsPrimitive()).thenReturn(false);
        when(invocation.isValidReturnType(String.class)).thenReturn(false);
        when(invocation.printMethodReturnType()).thenReturn("Integer");
        when(invocation.getMethodName()).thenReturn("number");

        new AnswersValidator().validate(new Returns("value"), invocation);
    }

    @Test
    public void shouldAllowRuntimeException() {
        Invocation invocation = mock(Invocation.class);

        new AnswersValidator().validate(new ThrowsException(new IllegalStateException()), invocation);
    }

    @Test
    public void shouldAllowDeclaredCheckedException() {
        Invocation invocation = mock(Invocation.class);
        IOException exception = new IOException();
        when(invocation.isValidException(exception)).thenReturn(true);

        new AnswersValidator().validate(new ThrowsException(exception), invocation);
    }

    @Test(expected = MockitoException.class)
    public void shouldRejectUndeclaredCheckedException() {
        Invocation invocation = mock(Invocation.class);
        IOException exception = new IOException();
        when(invocation.isValidException(exception)).thenReturn(false);

        new AnswersValidator().validate(new ThrowsException(exception), invocation);
    }

    @Test(expected = MockitoException.class)
    public void shouldRejectNullThrowable() {
        Invocation invocation = mock(Invocation.class);

        new AnswersValidator().validate(new ThrowsException(null), invocation);
    }
}