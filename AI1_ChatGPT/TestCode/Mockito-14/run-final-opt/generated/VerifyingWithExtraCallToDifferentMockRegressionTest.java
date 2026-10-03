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

    @Test
    public void shouldAllowVerifyingWhenOtherMockCallIsInTheSameLine() {
        Target target = mock(Target.class);
        Other other = mock(Other.class);
        target.call(null);

        verify(target).call(other.value());

        verify(other).value();
        verifyNoMoreInteractions(target, other);
    }

    @Test
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
}
