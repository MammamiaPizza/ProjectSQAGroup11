package org.mockito.internal.invocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mockito;

public class InvocationRealMethodInterfaceTest {

    public interface IndexedValues {
        String valueAt(int index);
    }

    public static class ConcreteValues {
        public String valueAt(int index) {
            return "value-" + index;
        }
    }

    @Test
    public void shouldFailAtRuntimeWhenCallingRealInterfaceMethodWithArguments() {
        IndexedValues values = Mockito.mock(IndexedValues.class, Mockito.CALLS_REAL_METHODS);

        Throwable failure = null;
        try {
            values.valueAt(7);
        } catch (Throwable caught) {
            failure = caught;
        }

        if (failure == null) {
            fail("Calling a real method on an interface must fail");
        }

        assertNotNull(failure);
        assertFalse("Interface real-method calls must not fail with NullPointerException",
                failure instanceof NullPointerException);
        assertFalse("Interface real-method calls must not fail with NoSuchMethodError",
                failure instanceof NoSuchMethodError);
        assertTrue("Interface real-method calls must report a runtime failure",
                failure instanceof RuntimeException);
    }

    @Test
    public void shouldCallRealMethodForConcreteClassWithArguments() {
        ConcreteValues values = Mockito.mock(ConcreteValues.class, Mockito.CALLS_REAL_METHODS);

        assertEquals("value-3", values.valueAt(3));
    }
}
