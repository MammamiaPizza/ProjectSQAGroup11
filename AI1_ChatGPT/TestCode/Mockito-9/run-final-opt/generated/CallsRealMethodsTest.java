import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.mockito.Mockito.CALLS_REAL_METHODS;

import org.junit.Test;

public class CallsRealMethodsTest {

    public interface InterfaceView {
        String interfaceMethod();
    }

    public static abstract class AbstractType implements InterfaceView {
        private final String value;

        public AbstractType() {
            this.value = "constructed";
        }

        @Override
        public String interfaceMethod() {
            return value;
        }

        public abstract String abstractText();

        public abstract int abstractNumber();

        public abstract boolean abstractFlag();
    }

    private AbstractType constructorMock() {
        return mock(AbstractType.class,
                withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    public void callsRealImplementationOfMethodDeclaredOnInterface() {
        InterfaceView mock = constructorMock();

        assertEquals("constructed", mock.interfaceMethod());
    }

    @Test
    public void stubbedAbstractMethodReturnsConfiguredValue() {
        AbstractType mock = constructorMock();
        when(mock.abstractText()).thenReturn("stubbed value");

        assertEquals("stubbed value", mock.abstractText());
    }

    @Test
    public void unstubbedAbstractReferenceMethodReturnsMockitoDefault() {
        AbstractType mock = constructorMock();

        assertNull(mock.abstractText());
    }

    @Test
    public void unstubbedAbstractPrimitiveMethodsReturnMockitoDefaults() {
        AbstractType mock = constructorMock();

        assertEquals(0, mock.abstractNumber());
        assertFalse(mock.abstractFlag());
    }
}
