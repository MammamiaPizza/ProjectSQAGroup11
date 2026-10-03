package org.apache.commons.lang3.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

public class ExtendedMessageFormatRegistryTest {

    @Test
    public void equalFormatsWithEqualRegistryContentsHaveEqualHashCodes() {
        Map<String, FormatFactory> firstRegistry = new HashMap<String, FormatFactory>();
        firstRegistry.put("padded", new PaddingFactory("first", 17));

        Map<String, FormatFactory> secondRegistry = new HashMap<String, FormatFactory>();
        secondRegistry.put("padded", new PaddingFactory("first", 17));

        ExtendedMessageFormat first =
                new ExtendedMessageFormat("Value: {0,padded}", Locale.US, firstRegistry);
        ExtendedMessageFormat second =
                new ExtendedMessageFormat("Value: {0,padded}", Locale.US, secondRegistry);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void differentRegistryContentsMakeFormatsUnequalAndChangeHashCode() {
        Map<String, FormatFactory> firstRegistry = new HashMap<String, FormatFactory>();
        firstRegistry.put("first", new PaddingFactory("first", 17));

        Map<String, FormatFactory> secondRegistry = new HashMap<String, FormatFactory>();
        secondRegistry.put("second", new PaddingFactory("second", 37));

        ExtendedMessageFormat first =
                new ExtendedMessageFormat("Value: {0}", Locale.US, firstRegistry);
        ExtendedMessageFormat second =
                new ExtendedMessageFormat("Value: {0}", Locale.US, secondRegistry);

        assertFalse(first.equals(second));
        assertFalse(first.hashCode() == second.hashCode());
    }

    @Test
    public void nullAndEmptyRegistriesAreNotEqual() {
        ExtendedMessageFormat withoutRegistry =
                new ExtendedMessageFormat("Value: {0}", Locale.US, null);
        ExtendedMessageFormat withEmptyRegistry =
                new ExtendedMessageFormat("Value: {0}", Locale.US,
                        new HashMap<String, FormatFactory>());

        assertFalse(withoutRegistry.equals(withEmptyRegistry));
    }

    @Test
    public void registeredFactoryFormatsValueAndPreservesCustomPattern() {
        Map<String, FormatFactory> registry = new HashMap<String, FormatFactory>();
        registry.put("padded", new PaddingFactory("padded", 11));

        ExtendedMessageFormat format =
                new ExtendedMessageFormat("Value: {0,padded}", Locale.US, registry);

        assertEquals("Value: 007", format.format(new Object[] { Integer.valueOf(7) }));
        assertEquals("Value: {0,padded}", format.toPattern());
    }

    @Test
    public void ordinaryMessageFormatPatternWorksWithoutRegistry() {
        ExtendedMessageFormat format =
                new ExtendedMessageFormat("Hello {0}", Locale.US);

        assertEquals("Hello world", format.format(new Object[] { "world" }));
    }

    @Test
    public void formatMutationIsUnsupported() {
        ExtendedMessageFormat format =
                new ExtendedMessageFormat("Value: {0}", Locale.US);

        try {
            format.setFormat(0, new DecimalFormat("000"));
            fail("ExtendedMessageFormat must reject format mutation");
        } catch (UnsupportedOperationException expected) {
            assertEquals(UnsupportedOperationException.class, expected.getClass());
        }
    }

    private static final class PaddingFactory implements FormatFactory {
        private final String id;
        private final int hashCode;

        private PaddingFactory(String id, int hashCode) {
            this.id = id;
            this.hashCode = hashCode;
        }

        public Format getFormat(String name, String arguments, Locale locale) {
            return new DecimalFormat("000");
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof PaddingFactory)) {
                return false;
            }
            PaddingFactory other = (PaddingFactory) object;
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}