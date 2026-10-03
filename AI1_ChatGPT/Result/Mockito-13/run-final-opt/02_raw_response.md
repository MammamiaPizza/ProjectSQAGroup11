import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class MockHandlerVerificationTest {

    interface Target {
        void accept(Object value);
    }

    interface Other {
        Object value();
    }

    @Test
    public void shouldAllowVerificationWhenArgumentIsObtainedFromDifferentMockInSameStatement() {
        Target target = mock(Target.class);
        Other other = mock(Other.class);

        target.accept(null);

        verify(target).accept(other.value());
        verify(other).value();
    }

    @Test
    public void shouldVerifyInvocationOnTargetMockNormally() {
        Target target = mock(Target.class);

        target.accept("value");

        verify(target).accept("value");
    }
}