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

@org.junit.Test
public void shouldExplainWhyRealMethodsCannotBeCalledOnInterfaces() {
    try {
        new org.mockito.exceptions.Reporter().cannotCallRealMethodOnInterface();
        org.junit.Assert.fail("Expected a MockitoException");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("Cannot call real method on java interface"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("mocking concrete classes"));
    }
}

@org.junit.Test
public void shouldDescribeFailureToInitializeSpyField() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForSpyAnnotation("spiedField", new java.lang.Exception("no default constructor"));
        org.junit.Assert.fail("Expected a MockitoException");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("@Spy for 'spiedField'"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("no default constructor"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("Examples of correct usage of @Spy"));
    }
}

@org.junit.Test
public void shouldDescribeFailureToInitializeInjectMocksField() {
    try {
        new org.mockito.exceptions.Reporter().cannotInitializeForInjectMocksAnnotation("service", new java.lang.Exception("construction failed"));
        org.junit.Assert.fail("Expected a MockitoException");
    } catch (RuntimeException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().contains("@InjectMocks field named 'service'"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("construction failed"));
        org.junit.Assert.assertTrue(expected.getMessage().contains("Examples of correct usage of @InjectMocks"));
    }
}
}
