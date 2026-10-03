package org.mockito.exceptions;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.lang.reflect.Field;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ReporterBogusDefaultAnswerTest {

    private interface Service {
        void call();
    }

    private static class InjectionTarget {
        private Service dependency;
    }

    private final Answer<Object> bogusDefaultAnswer = new Answer<Object>() {
        public Object answer(InvocationOnMock invocation) {
            return new Object();
        }
    };

    @Test(expected = NoInteractionsWanted.class)
    public void reportsNoInteractionsWantedInsteadOfClassCastExceptionForBogusDefaultAnswer() {
        Service service = mock(Service.class, bogusDefaultAnswer);

        service.call();

        verifyZeroInteractions(service);
    }

    @Test(expected = VerificationInOrderFailure.class)
    public void reportsInOrderFailureInsteadOfClassCastExceptionForBogusDefaultAnswer() {
        Service service = mock(Service.class, bogusDefaultAnswer);
        InOrder inOrder = inOrder(service);

        service.call();

        inOrder.verifyNoMoreInteractions();
    }

    @Test(expected = MockitoException.class)
    public void reportsInjectionFailureInsteadOfNullPointerExceptionForBogusDefaultAnswer()
            throws Exception {
        Service service = mock(Service.class, bogusDefaultAnswer);
        Field field = InjectionTarget.class.getDeclaredField("dependency");

        new Reporter().cannotInjectDependency(field, service, new Exception("injection failed"));
    }
}
