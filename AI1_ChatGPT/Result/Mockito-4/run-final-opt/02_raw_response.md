package org.mockito.exceptions;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.NoInteractionsWanted;
import org.mockito.exceptions.verification.VerificationInOrderFailure;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ReporterBug187Test {

    private static class InjectionTarget {
        List dependency;
    }

    private List createMockWithBogusDefaultAnswer() {
        return mock(List.class, new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                return "bogus default answer";
            }
        });
    }

    @Test(expected = NoInteractionsWanted.class)
    public void reportsNoMoreInteractionsWantedInsteadOfClassCastExceptionForBogusDefaultAnswer() {
        List mock = createMockWithBogusDefaultAnswer();

        mock.clear();

        verifyNoMoreInteractions(mock);
    }

    @Test(expected = NoInteractionsWanted.class)
    public void reportsZeroInteractionsFailureInsteadOfClassCastExceptionForBogusDefaultAnswer() {
        List mock = createMockWithBogusDefaultAnswer();

        mock.clear();

        verifyZeroInteractions(mock);
    }

    @Test(expected = VerificationInOrderFailure.class)
    public void reportsInOrderNoMoreInteractionsFailureInsteadOfClassCastExceptionForBogusDefaultAnswer() {
        List mock = createMockWithBogusDefaultAnswer();
        InOrder order = inOrder(mock);

        mock.clear();

        order.verifyNoMoreInteractions();
    }

    @Test(expected = MockitoException.class)
    public void reportsInjectionFailureInsteadOfNullPointerExceptionForBogusDefaultAnswer() throws Exception {
        List mock = createMockWithBogusDefaultAnswer();
        Field field = InjectionTarget.class.getDeclaredField("dependency");

        new Reporter().cannotInjectDependency(field, mock, new Exception("injection failed"));
    }

    @Test
    public void permitsNoMoreInteractionsCheckAfterBogusDefaultAnswerInteractionIsVerified() {
        List mock = createMockWithBogusDefaultAnswer();

        mock.clear();

        List verificationProxy = verify(mock);
        verificationProxy.clear();
        verifyNoMoreInteractions(mock);

        assertSame(mock, verificationProxy);
    }
}