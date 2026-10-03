package org.mockito.internal.stubbing.defaultanswers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.mockito.Mockito;

public class ReturnsSmartNullsGeneratedTest {

    public interface Child {
        void ping();
    }

    public interface Sample {
        Child withArgs(String first, String second);

        Child noArgs();

        String text();

        Integer number();
    }

    @Test
    public void smartNullDescriptionIncludesArgumentsOfUnstubbedInvocation() {
        Sample sample = Mockito.mock(Sample.class, new ReturnsSmartNulls());

        Child result = sample.withArgs("oompa", "lumpa");

        assertNotNull(result);
        assertEquals(
                "SmartNull returned by unstubbed withArgs([oompa, lumpa]) method on mock",
                result.toString());
    }

    @Test
    public void smartNullDescriptionRetainsEmptyArgumentListFormatting() {
        Sample sample = Mockito.mock(Sample.class, new ReturnsSmartNulls());

        Child result = sample.noArgs();

        assertNotNull(result);
        assertEquals(
                "SmartNull returned by unstubbed noArgs([]) method on mock",
                result.toString());
    }

    @Test
    public void returnsOrdinaryEmptyValueWhenDelegateCanProvideOne() {
        Sample sample = Mockito.mock(Sample.class, new ReturnsSmartNulls());

        assertEquals("", sample.text());
    }

    @Test
    public void returnsNullForNonMockableFinalReturnType() {
        Sample sample = Mockito.mock(Sample.class, new ReturnsSmartNulls());

        assertNull(sample.number());
    }

    @Test(expected = RuntimeException.class)
    public void invokingMethodOnSmartNullThrowsDiagnosticException() {
        Sample sample = Mockito.mock(Sample.class, new ReturnsSmartNulls());
        Child result = sample.noArgs();

        assertNotNull(result);
        result.ping();
    }
}
