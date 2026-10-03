package org.mockitousage.bugs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.mockito.exceptions.misusing.NotAMockException;

public class VerifyingWithExtraCallToDifferentMockRegressionTest {

    public interface Target {
        void call(String value);
    }

    public interface Other {
        String value();
    }

    @Test
    public void shouldVerifyNormalInvocationOnSingleMock() {
        Target target = mock(Target.class);

        target.call("value");

        verify(target).call("value");
        verifyNoMoreInteractions(target);
    }

    @Test(expected = AssertionError.class)
    public void shouldAllowVerifyingWhenOtherMockCallIsInTheSameLine() {
        Target target = mock(Target.class);
        Other other = mock(Other.class);
        target.call(null);

        verify(target).call(other.value());

        verify(other).value();
        verifyNoMoreInteractions(target, other);
    }

    @Test(expected = AssertionError.class)
    public void shouldKeepVerificationModeForTargetWhenOtherMockCallOccursDuringVerification() {
        Target target = mock(Target.class);
        Other other = mock(Other.class);
        target.call(null);
        target.call(null);

        verify(target, times(2)).call(other.value());

        verify(other).value();
        verifyNoMoreInteractions(target, other);
    }

    @Test
    public void shouldVerifyTargetAfterAnEarlierInvocationOnAnotherMock() {
        Target target = mock(Target.class);
        Other other = mock(Other.class);

        String argument = other.value();
        target.call(argument);

        verify(target).call(null);
        verify(other).value();
        verifyNoMoreInteractions(target, other);
    }

    @Test(expected = AssertionError.class)
    public void shouldFailVerificationWhenWantedInvocationDidNotOccur() {
        Target target = mock(Target.class);

        verify(target).call("missing");
    }

    @Test(expected = NotAMockException.class)
    public void shouldRejectVerificationOfANonMockObject() {
        verify(new Object());
    }

@org.junit.Test
public void shouldReturnValueConfiguredWithWhen() {
    java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);

    org.mockito.Mockito.when(mock.get(0)).thenReturn("stubbed");

    org.junit.Assert.assertEquals("stubbed", mock.get(0));
}

@org.junit.Test
public void shouldResetRecordedInteractions() {
    java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
    mock.add("one");

    org.mockito.Mockito.reset(mock);

    org.mockito.Mockito.verifyNoMoreInteractions(mock);
}

@org.junit.Test(expected = org.mockito.exceptions.verification.NoInteractionsWanted.class)
public void shouldReportUnverifiedInteractionsWhenCheckingForNoMoreInteractions() {
    java.util.List<String> mock = org.mockito.Mockito.mock(java.util.List.class);
    mock.add("one");

    org.mockito.Mockito.verifyNoMoreInteractions(mock);
}

@org.junit.Test
public void shouldVerifyInvocationsAcrossMocksInOrder() {
    java.util.List<String> first = org.mockito.Mockito.mock(java.util.List.class);
    java.util.List<String> second = org.mockito.Mockito.mock(java.util.List.class);
    first.add("first");
    second.add("second");

    org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(first, second);
    inOrder.verify(first).add("first");
    inOrder.verify(second).add("second");
}
}
