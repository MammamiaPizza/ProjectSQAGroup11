package org.joda.time;

import junit.framework.TestCase;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.UnsupportedDurationField;

public class Time1RegressionTest extends TestCase {

    public void testUnsupportedDurationFieldIsCachedAndReportsUnsupported() {
        UnsupportedDurationField first =
                UnsupportedDurationField.getInstance(DurationFieldType.eras());
        UnsupportedDurationField second =
                UnsupportedDurationField.getInstance(DurationFieldType.eras());

        assertSame(first, second);
        assertEquals(DurationFieldType.eras(), first.getType());
        assertEquals("eras", first.getName());
        assertFalse(first.isSupported());
        assertTrue(first.isPrecise());
        assertEquals("UnsupportedDurationField[eras]", first.toString());
    }

    public void testUnsupportedDurationFieldOperationsThrowUnsupportedOperationException() {
        UnsupportedDurationField field =
                UnsupportedDurationField.getInstance(DurationFieldType.eras());

        try {
            field.getValue(1L);
            fail("Unsupported duration fields must reject value conversion");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().indexOf("eras") >= 0);
        }

        try {
            field.add(0L, 1);
            fail("Unsupported duration fields must reject addition");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().indexOf("eras") >= 0);
        }

        try {
            field.getDifference(1L, 0L);
            fail("Unsupported duration fields must reject difference calculation");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().indexOf("eras") >= 0);
        }
    }

    public void testUnsupportedDurationComparisonIsConsistentWithSupportedDuration() {
        DurationField unsupported =
                UnsupportedDurationField.getInstance(DurationFieldType.eras());
        DurationField years =
                DurationFieldType.years().getField(ISOChronology.getInstanceUTC());

        assertEquals(0, unsupported.compareTo(years));
        assertEquals(0, unsupported.compareTo(
                UnsupportedDurationField.getInstance(DurationFieldType.eras())));
    }

    public void testPartialRejectsYearBeforeEraBecauseTypesMustBeLargestToSmallest() {
        try {
            new Partial(
                    new DateTimeFieldType[] {
                        DateTimeFieldType.yearOfEra(),
                        DateTimeFieldType.era()
                    },
                    new int[] { 2020, 1 });
            fail("A year field must not precede the larger era field");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("largest-smallest") >= 0);
        }
    }

    public void testPartialAcceptsEraBeforeYearOfEra() {
        Partial partial = new Partial(
                new DateTimeFieldType[] {
                    DateTimeFieldType.era(),
                    DateTimeFieldType.yearOfEra()
                },
                new int[] { 1, 2020 });

        assertEquals(2, partial.size());
        assertEquals(DateTimeFieldType.era(), partial.getFieldType(0));
        assertEquals(DateTimeFieldType.yearOfEra(), partial.getFieldType(1));
        assertEquals(1, partial.getValue(0));
        assertEquals(2020, partial.getValue(1));
    }

    public void testPartialConstructorDefensivelyCopiesTypeAndValueArrays() {
        DateTimeFieldType[] types = new DateTimeFieldType[] {
            DateTimeFieldType.year(),
            DateTimeFieldType.monthOfYear()
        };
        int[] values = new int[] { 2020, 2 };

        Partial partial = new Partial(types, values);
        types[0] = DateTimeFieldType.dayOfMonth();
        values[0] = 1;

        assertEquals(DateTimeFieldType.year(), partial.getFieldType(0));
        assertEquals(2020, partial.getValue(0));
    }

    public void testPartialConstructorRejectsNullAndMismatchedArrays() {
        try {
            new Partial((DateTimeFieldType[]) null, new int[0]);
            fail("Null type arrays must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("Types array") >= 0);
        }

        try {
            new Partial(new DateTimeFieldType[0], (int[]) null);
            fail("Null value arrays must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("Values array") >= 0);
        }

        try {
            new Partial(
                    new DateTimeFieldType[] { DateTimeFieldType.year() },
                    new int[0]);
            fail("Type and value arrays of different lengths must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("same length") >= 0);
        }
    }

    public void testPartialConstructorRejectsNullTypeAndInvalidValue() {
        try {
            new Partial(
                    new DateTimeFieldType[] { null },
                    new int[] { 1 });
            fail("Null field types must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("must not contain null") >= 0);
        }

        try {
            new Partial(DateTimeFieldType.dayOfMonth(), 32);
            fail("Values outside a field's valid range must be rejected");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    public void testPartialConstructorRejectsRegularFieldsInSmallestToLargestOrder() {
        try {
            new Partial(
                    new DateTimeFieldType[] {
                        DateTimeFieldType.dayOfMonth(),
                        DateTimeFieldType.monthOfYear()
                    },
                    new int[] { 1, 1 });
            fail("Fields supplied smallest-to-largest must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("largest-smallest") >= 0);
        }
    }
}