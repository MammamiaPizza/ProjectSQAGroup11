package org.mockito.internal.matchers;

import org.hamcrest.StringDescription;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SameTest {

    @Test
    public void shouldMatchNullWhenWantedValueIsNull() {
        assertTrue(new Same(null).matches(null));
    }

    @Test
    public void shouldNotMatchNonNullActualWhenWantedValueIsNull() {
        assertFalse(new Same(null).matches(new Object()));
    }

    @Test
    public void shouldNotMatchNullActualWhenWantedValueIsNonNull() {
        assertFalse(new Same(new Object()).matches(null));
    }

    @Test
    public void shouldMatchOnlyTheSameReference() {
        String wanted = new String("value");
        String equalButDifferent = new String("value");

        assertTrue(new Same(wanted).matches(wanted));
        assertFalse(new Same(wanted).matches(equalButDifferent));
    }

    @Test
    public void shouldDescribeNullWantedValueWithoutThrowing() {
        StringDescription description = new StringDescription();

        new Same(null).describeTo(description);

        assertEquals("same(null)", description.toString());
    }

    @Test
    public void shouldQuoteStringWantedValueInDescription() {
        StringDescription description = new StringDescription();

        new Same("value").describeTo(description);

        assertEquals("same(\"value\")", description.toString());
    }

    @Test
    public void shouldQuoteCharacterWantedValueInDescription() {
        StringDescription description = new StringDescription();

        new Same(Character.valueOf('x')).describeTo(description);

        assertEquals("same('x')", description.toString());
    }
}
