package org.joda.time;

import junit.framework.TestCase;

public class PartialGeneratedTest extends TestCase {

    public void testWithAddsYearBeforeExistingMonthAndDayFields() {
        Partial original = new Partial(
                new DateTimeFieldType[] {
                        DateTimeFieldType.monthOfYear(),
                        DateTimeFieldType.dayOfMonth()
                },
                new int[] {6, 9});

        Partial result = original.with(DateTimeFieldType.year(), 2004);

        assertEquals(3, result.size());
        assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
        assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(1));
        assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(2));
        assertEquals(2004, result.getValue(0));
        assertEquals(6, result.getValue(1));
        assertEquals(9, result.getValue(2));
        assertEquals(2, original.size());
        assertEquals(6, original.getValue(0));
        assertEquals(9, original.getValue(1));
    }

    public void testWithAddsSameDurationFieldUsingRangeOrdering() {
        Partial original = new Partial(
                new DateTimeFieldType[] {
                        DateTimeFieldType.monthOfYear(),
                        DateTimeFieldType.dayOfMonth()
                },
                new int[] {6, 9});

        Partial result = original.with(DateTimeFieldType.dayOfWeek(), 2);

        assertEquals(3, result.size());
        assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(0));
        assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(1));
        assertEquals(DateTimeFieldType.dayOfWeek(), result.getFieldType(2));
        assertEquals(6, result.getValue(0));
        assertEquals(9, result.getValue(1));
        assertEquals(2, result.getValue(2));
    }

    public void testWithReplacesExistingFieldWithoutChangingOriginal() {
        Partial original = new Partial(
                new DateTimeFieldType[] {
                        DateTimeFieldType.year(),
                        DateTimeFieldType.monthOfYear(),
                        DateTimeFieldType.dayOfMonth()
                },
                new int[] {2004, 6, 9});

        Partial result = original.with(DateTimeFieldType.monthOfYear(), 12);

        assertEquals(3, result.size());
        assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
        assertEquals(DateTimeFieldType.monthOfYear(), result.getFieldType(1));
        assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(2));
        assertEquals(2004, result.getValue(0));
        assertEquals(12, result.getValue(1));
        assertEquals(9, result.getValue(2));
        assertEquals(6, original.getValue(1));
    }

    public void testWithFieldChangesOnlySupportedField() {
        Partial original = new Partial(
                new DateTimeFieldType[] {
                        DateTimeFieldType.year(),
                        DateTimeFieldType.monthOfYear(),
                        DateTimeFieldType.dayOfMonth()
                },
                new int[] {2004, 6, 9});

        Partial result = original.withField(DateTimeFieldType.dayOfMonth(), 30);

        assertEquals(3, result.size());
        assertEquals(2004, result.getValue(0));
        assertEquals(6, result.getValue(1));
        assertEquals(30, result.getValue(2));
        assertEquals(9, original.getValue(2));
    }

    public void testWithoutRemovesPresentFieldAndRetainsOthers() {
        Partial original = new Partial(
                new DateTimeFieldType[] {
                        DateTimeFieldType.year(),
                        DateTimeFieldType.monthOfYear(),
                        DateTimeFieldType.dayOfMonth()
                },
                new int[] {2004, 6, 9});

        Partial result = original.without(DateTimeFieldType.monthOfYear());

        assertEquals(2, result.size());
        assertEquals(DateTimeFieldType.year(), result.getFieldType(0));
        assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(1));
        assertEquals(2004, result.getValue(0));
        assertEquals(9, result.getValue(1));
        assertEquals(3, original.size());
        assertEquals(DateTimeFieldType.monthOfYear(), original.getFieldType(1));
        assertEquals(6, original.getValue(1));
    }

    public void testWithRejectsInvalidFieldValue() {
        Partial original = new Partial(DateTimeFieldType.monthOfYear(), 6);

        try {
            original.with(DateTimeFieldType.monthOfYear(), 13);
            fail("Expected an exception for an invalid month value");
        } catch (IllegalArgumentException expected) {
        }

        assertEquals(6, original.getValue(0));
    }

    public void testWithFieldRejectsUnsupportedField() {
        Partial original = new Partial(DateTimeFieldType.monthOfYear(), 6);

        try {
            original.withField(DateTimeFieldType.year(), 2004);
            fail("Expected an exception for an unsupported field");
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testConstructorRejectsFieldsInSmallestToLargestOrder() {
        try {
            new Partial(
                    new DateTimeFieldType[] {
                            DateTimeFieldType.dayOfMonth(),
                            DateTimeFieldType.monthOfYear()
                    },
                    new int[] {9, 6});
            fail("Expected an exception for fields in the wrong order");
        } catch (IllegalArgumentException expected) {
        }
    }
}