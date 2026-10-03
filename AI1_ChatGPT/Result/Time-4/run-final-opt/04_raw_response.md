public void testEmptyConstructorCreatesEmptyPartial() {
    Partial partial = new Partial();

    assertEquals(0, partial.size());
    assertEquals(0, partial.getFieldTypes().length);
    assertEquals(0, partial.getValues().length);
}

public void testArrayConstructorRejectsInvalidArguments() {
    try {
        new Partial((org.joda.time.DateTimeFieldType[]) null, new int[0]);
        fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
        new Partial(new org.joda.time.DateTimeFieldType[0], (int[]) null);
        fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
        new Partial(
                new org.joda.time.DateTimeFieldType[] {
                    org.joda.time.DateTimeFieldType.monthOfYear()
                },
                new int[0]);
        fail();
    } catch (IllegalArgumentException ex) {
    }
}

public void testCopyConstructorCopiesFieldsAndValues() {
    Partial source = new Partial(
            new org.joda.time.DateTimeFieldType[] {
                org.joda.time.DateTimeFieldType.monthOfYear(),
                org.joda.time.DateTimeFieldType.dayOfMonth()
            },
            new int[] {6, 9});

    Partial copy = new Partial(source);

    assertEquals(2, copy.size());
    assertEquals(org.joda.time.DateTimeFieldType.monthOfYear(), copy.getFieldType(0));
    assertEquals(org.joda.time.DateTimeFieldType.dayOfMonth(), copy.getFieldType(1));
    assertEquals(6, copy.getValue(0));
    assertEquals(9, copy.getValue(1));

    try {
        new Partial((org.joda.time.ReadablePartial) null);
        fail();
    } catch (IllegalArgumentException ex) {
    }
}

public void testWithInsertsLargerFieldBeforeExistingTimeField() {
    Partial original = new Partial(org.joda.time.DateTimeFieldType.hourOfDay(), 10);

    Partial result = original.with(org.joda.time.DateTimeFieldType.year(), 2004);

    assertEquals(2, result.size());
    assertEquals(org.joda.time.DateTimeFieldType.year(), result.getFieldType(0));
    assertEquals(org.joda.time.DateTimeFieldType.hourOfDay(), result.getFieldType(1));
    assertEquals(2004, result.getValue(0));
    assertEquals(10, result.getValue(1));
    assertEquals(1, original.size());
    assertEquals(10, original.getValue(0));
}