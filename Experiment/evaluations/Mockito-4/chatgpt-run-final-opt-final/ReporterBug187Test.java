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

@org.junit.Test
public void reportsCannotCallAbstractRealMethod() {
    try {
        new org.mockito.exceptions.Reporter().cannotCallAbstractRealMethod();
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Cannot call abstract real method on java object!"));
    }
}

@org.junit.Test
public void reportsSpyInitializationFailureDetails() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForSpyAnnotation(
                "spyField", new java.lang.Exception("construction failed"));
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Cannot instantiate a @Spy for 'spyField' field."));
        org.junit.Assert.assertTrue(e.getMessage().contains("construction failed"));
    }
}

@org.junit.Test
public void reportsInjectMocksInitializationFailureDetails() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForInjectMocksAnnotation(
                "service", new java.lang.Exception("no suitable constructor"));
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Cannot instantiate @InjectMocks field named 'service'."));
        org.junit.Assert.assertTrue(e.getMessage().contains("no suitable constructor"));
    }
}

@org.junit.Test
public void reportsFriendlyReminderWhenAtMostOrNeverIsUsedWithTimeout() {
    try {
        new org.mockito.exceptions.Reporter().atMostAndNeverShouldNotBeUsedWithTimeout();
        org.junit.Assert.fail("Expected MockitoException");
    } catch (org.mockito.exceptions.base.MockitoException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("timeout() should not be used with atMost() or never()"));
    }
}
}
