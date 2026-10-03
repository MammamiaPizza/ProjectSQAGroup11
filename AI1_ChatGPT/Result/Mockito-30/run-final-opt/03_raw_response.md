package org.mockito.internal.stubbing.defaultanswers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.SmartNullPointerException;

public class ReturnsSmartNullsBug225Test {

    public interface Root {
        Child child();

        Child childWithOrigin(String origin);

        Child childWithOrigin(String first, String second);

        int count();

        String text();

        BigDecimal amount();
    }

    public interface Child {
        void accept(String value);

        void acceptBoth(String first, String second);

        void noArguments();
    }

    private Root smartMock() {
        return Mockito.mock(Root.class, new ReturnsSmartNulls());
    }

    @Test
    public void shouldIncludeAllArgumentsOfMethodCalledOnSmartNullInExceptionMessage() {
        Root mock = smartMock();

        try {
            mock.childWithOrigin("oompa", "lumpa").noArguments();
            fail("Expected a SmartNullPointerException");
        } catch (SmartNullPointerException expected) {
            String message = String.valueOf(expected.getMessage());
            assertTrue(message.contains("oompa"));
            assertTrue(message.contains("lumpa"));
        }
    }

    @Test
    public void shouldIncludeSingleArgumentOfMethodCalledOnSmartNullInExceptionMessage() {
        Root mock = smartMock();

        try {
            mock.childWithOrigin("only-value").noArguments();
            fail("Expected a SmartNullPointerException");
        } catch (SmartNullPointerException expected) {
            assertTrue(String.valueOf(expected.getMessage()).contains("only-value"));
        }
    }

    @Test
    public void shouldThrowSmartNullPointerExceptionForZeroArgumentMethodCalledOnSmartNull() {
        Root mock = smartMock();

        try {
            mock.child().noArguments();
            fail("Expected a SmartNullPointerException");
        } catch (SmartNullPointerException expected) {
            assertTrue(expected.getMessage() != null && expected.getMessage().length() > 0);
        }
    }

    @Test
    public void shouldDescribeOriginatingUnstubbedInvocationWhenSmartNullIsConvertedToString() {
        Root mock = smartMock();

        String description = mock.childWithOrigin("origin").toString();

        assertTrue(description.contains("childWithOrigin(origin)"));
    }

    @Test
    public void shouldDelegatePrimitiveAndStringDefaultsToMoreEmptyValues() {
        Root mock = smartMock();

        assertEquals(0, mock.count());
        assertEquals("", mock.text());
    }

    @Test
    public void shouldReturnEmptyValueForBigDecimal() {
        Root mock = smartMock();

        assertEquals(BigDecimal.ZERO, mock.amount());
    }
}