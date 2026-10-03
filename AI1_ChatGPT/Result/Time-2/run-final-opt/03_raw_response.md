package org.joda.time;

import junit.framework.TestCase;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.UnsupportedDurationField;

public class UnsupportedDurationFieldPartialTest extends TestCase {

    public void testEraAndYearCanFormOrderedPartial() {
        Partial partial = new Partial(
                new DateTimeFieldType[] {
                        DateTimeFieldType.era(),
                        DateTimeFieldType.year()
                },
                new int[] { 1, 2000 },
                ISOChronology.getInstanceUTC());

        assertEquals(2, partial.size());
        assertEquals(DateTimeFieldType.era(), partial.getFieldType(0));
        assertEquals(1, partial.getValue(0));
        assertEquals(DateTimeFieldType.year(), partial.getFieldType(1));
        assertEquals(2000, partial.getValue(1));
    }

    public void testUnsupportedEraDurationOrdersAfterSupportedYearDuration() {
        DurationField eraDuration =
                UnsupportedDurationField.getInstance(DurationFieldType.eras());
        DurationField yearDuration =
                DateTimeFieldType.year().getDurationType().getField(ISOChronology.getInstanceUTC());

        assertTrue(eraDuration.compareTo(yearDuration) > 0);
    }

    public void testYearBeforeEraIsRejectedAsOutOfOrder() {
        try {
            new Partial(
                    new DateTimeFieldType[] {
                            DateTimeFieldType.year(),
                            DateTimeFieldType.era()
                    },
                    new int[] { 2000, 1 },
                    ISOChronology.getInstanceUTC());
            fail("Year followed by era is not largest-to-smallest field order");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("largest-smallest") >= 0);
        }
    }

    public void testDuplicateEraFieldsAreRejected() {
        try {
            new Partial(
                    new DateTimeFieldType[] {
                            DateTimeFieldType.era(),
                            DateTimeFieldType.era()
                    },
                    new int[] { 1, 1 },
                    ISOChronology.getInstanceUTC());
            fail("Duplicate field types must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("duplicate") >= 0);
        }
    }
}