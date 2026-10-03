package org.mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MatchersPrimitiveRegressionTest {

    private interface IntService {
        String primitive(int value);
        String boxed(Integer value);
    }

    @Test
    public void shouldAllowAnyIntWhenStubbingPrimitiveArgument() {
        IntService service = Mockito.mock(IntService.class);

        Mockito.when(service.primitive(Matchers.anyInt())).thenReturn("matched");

        assertEquals("matched", service.primitive(42));
    }

    @Test
    public void shouldAllowAnyIntWhenVerifyingPrimitiveArgument() {
        IntService service = Mockito.mock(IntService.class);

        service.primitive(-1);

        Mockito.verify(service).primitive(Matchers.anyInt());
    }

    @Test
    public void shouldAllowTypedAnyIntegerWhenPassedToPrimitiveArgument() {
        IntService service = Mockito.mock(IntService.class);

        Mockito.when(service.primitive(Matchers.any(Integer.class))).thenReturn("integer");

        assertEquals("integer", service.primitive(123));
    }

    @Test
    public void shouldAllowGenericEqIntegerWhenPassedToPrimitiveArgument() {
        IntService service = Mockito.mock(IntService.class);
        Integer expected = Integer.valueOf(9);

        Mockito.when(service.primitive(Matchers.<Integer>eq(expected))).thenReturn("equal");

        assertEquals("equal", service.primitive(9));
        assertNull(service.primitive(8));
    }

    @Test
    public void shouldAllowGenericSameIntegerWhenPassedToPrimitiveArgument() {
        IntService service = Mockito.mock(IntService.class);
        Integer expected = Integer.valueOf(17);

        Mockito.when(service.primitive(Matchers.<Integer>same(expected))).thenReturn("same");

        assertEquals("same", service.primitive(17));
    }

    @Test
    public void shouldStillAllowGenericAnyForBoxedIntegerArguments() {
        IntService service = Mockito.mock(IntService.class);

        Mockito.when(service.boxed(Matchers.<Integer>any())).thenReturn("boxed");

        assertEquals("boxed", service.boxed(Integer.valueOf(3)));
    }
}