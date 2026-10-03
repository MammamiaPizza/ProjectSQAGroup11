package org.mockito.internal.stubbing.answers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.Test;

public class CallsRealMethodsBugTest {

    @Test
    public void shouldReturnDefaultValueForUnstubbedAbstractMethod() {
        AbstractClass mock = mock(AbstractClass.class,
                withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));

        assertNull(mock.abstractValue());
    }

    @Test
    public void shouldAllowStubbingAbstractMethod() {
        AbstractClass mock = mock(AbstractClass.class,
                withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));

        when(mock.abstractValue()).thenReturn("stubbed");

        assertEquals("stubbed", mock.abstractValue());
    }

    @Test
    public void shouldCallConcreteMethodsAndUseConstructorState() {
        AbstractClass mock = mock(AbstractClass.class,
                withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));

        assertEquals("constructed", mock.concreteValue());
    }

    @Test
    public void shouldReturnDefaultForAbstractMethodDeclaredByInterface() {
        AbstractInterfaceHolder mock = mock(AbstractInterfaceHolder.class,
                withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));

        assertEquals(0, mock.number());
    }

    @Test
    public void shouldCallRealDefaultInterfaceMethod() {
        DefaultInterfaceHolder mock = mock(DefaultInterfaceHolder.class,
                withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));

        assertEquals("real-interface-method", mock.defaultValue());
    }

    public static abstract class AbstractClass {
        private final String value;

        public AbstractClass() {
            value = "constructed";
        }

        public abstract String abstractValue();

        public String concreteValue() {
            return value;
        }
    }

    public interface AbstractInterface {
        int number();
    }

    public static abstract class AbstractInterfaceHolder implements AbstractInterface {
        public AbstractInterfaceHolder() {
        }
    }

    public interface DefaultInterface {
        default String defaultValue() {
            return "real-interface-method";
        }
    }

    public static abstract class DefaultInterfaceHolder implements DefaultInterface {
        public DefaultInterfaceHolder() {
        }
    }
}
