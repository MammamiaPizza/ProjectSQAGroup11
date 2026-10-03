package org.apache.commons.codec.language;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DoubleMetaphoneCodec184Test {

    @Test
    public void testBasicEqualAndUnequalNames() {
        final DoubleMetaphone metaphone = new DoubleMetaphone();

        assertTrue(metaphone.isDoubleMetaphoneEqual("Case", "case"));
        assertTrue(metaphone.isDoubleMetaphoneEqual("Steve", "Steven"));
        assertFalse(metaphone.isDoubleMetaphoneEqual("case", "quick"));
        assertFalse(metaphone.isDoubleMetaphoneEqual("Steve", "Stevens"));
    }

    @Test
    public void testAlternateEqualityOverloadForEqualAndUnequalNames() {
        final DoubleMetaphone metaphone = new DoubleMetaphone();

        assertTrue(metaphone.isDoubleMetaphoneEqual("Case", "case", true));
        assertFalse(metaphone.isDoubleMetaphoneEqual("case", "quick", true));
    }

    @Test
    public void testEqualityHandlesSingleNullArguments() {
        final DoubleMetaphone metaphone = new DoubleMetaphone();

        assertFalse(metaphone.isDoubleMetaphoneEqual(null, "String"));
        assertFalse(metaphone.isDoubleMetaphoneEqual("String", null));
        assertFalse(metaphone.isDoubleMetaphoneEqual(null, "String", true));
        assertFalse(metaphone.isDoubleMetaphoneEqual("String", null, true));
    }

    @Test
    public void testEqualityTreatsTwoNullInputsAsEqual() {
        final DoubleMetaphone metaphone = new DoubleMetaphone();

        assertTrue(metaphone.isDoubleMetaphoneEqual(null, null));
        assertTrue(metaphone.isDoubleMetaphoneEqual(null, null, true));
    }

    @Test
    public void testDoubleMetaphoneReturnsNullForNullInput() {
        final DoubleMetaphone metaphone = new DoubleMetaphone();

        assertNull(metaphone.doubleMetaphone(null));
        assertNull(metaphone.doubleMetaphone(null, true));
        assertNull(metaphone.encode((String) null));
    }
}
