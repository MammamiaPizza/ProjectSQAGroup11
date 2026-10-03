package org.apache.commons.collections.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

public class CaseInsensitiveMapLocaleIndependenceTest {

    @Test
    public void testCaseInsensitiveOperationsAreIndependentOfTurkishAndAzeriLocales() {
        Locale original = Locale.getDefault();
        Locale[] sensitiveLocales = {
            new Locale("tr", "TR"),
            new Locale("az", "AZ")
        };

        try {
            for (int i = 0; i < sensitiveLocales.length; i++) {
                Locale.setDefault(Locale.ENGLISH);
                CaseInsensitiveMap map = new CaseInsensitiveMap();
                map.put("FILE", "value");

                Locale.setDefault(sensitiveLocales[i]);
                assertEquals("value", map.get("FILE"));
                assertEquals("value", map.remove("FILE"));
                assertFalse(map.containsKey("file"));

                CaseInsensitiveMap insertedUnderSensitiveLocale = new CaseInsensitiveMap();
                insertedUnderSensitiveLocale.put("i", "lowercase-i");
                assertEquals("lowercase-i", insertedUnderSensitiveLocale.get("I"));

                Locale.setDefault(Locale.ENGLISH);
                assertEquals("lowercase-i", insertedUnderSensitiveLocale.get("I"));
            }
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void testBasicCaseInsensitivePutGetAndReplacement() {
        CaseInsensitiveMap map = new CaseInsensitiveMap();

        assertNull(map.put("One", "first"));
        assertEquals("first", map.get("ONE"));
        assertTrue(map.containsKey("oNe"));

        assertEquals("first", map.put("one", "second"));
        assertEquals(1, map.size());
        assertEquals("second", map.get("ONE"));
    }

    @Test
    public void testNullKeyIsSupported() {
        CaseInsensitiveMap map = new CaseInsensitiveMap();

        assertNull(map.put(null, "null-value"));
        assertTrue(map.containsKey(null));
        assertEquals("null-value", map.get(null));
        assertTrue(map.keySet().contains(null));
    }

    @Test
    public void testKeySetExposesLowercaseStringKeys() {
        CaseInsensitiveMap map = new CaseInsensitiveMap();

        map.put("MiXeD", "value");

        assertTrue(map.keySet().contains("mixed"));
        assertFalse(map.keySet().contains("MiXeD"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroInitialCapacityIsRejected() {
        new CaseInsensitiveMap(0);
    }
}
