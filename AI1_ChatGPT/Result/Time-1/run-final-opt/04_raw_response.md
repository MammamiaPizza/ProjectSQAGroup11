public void testUnsupportedDurationFieldRejectsInstantBasedConversions() {
    org.joda.time.field.UnsupportedDurationField field =
        org.joda.time.field.UnsupportedDurationField.getInstance(org.joda.time.DurationFieldType.days());

    try {
        field.getValue(1000L, 0L);
        fail("Unsupported duration fields must reject instant-based value conversion");
    } catch (UnsupportedOperationException expected) {
    }

    try {
        field.getValueAsLong(1000L, 0L);
        fail("Unsupported duration fields must reject instant-based long value conversion");
    } catch (UnsupportedOperationException expected) {
    }

    try {
        field.getMillis(1, 0L);
        fail("Unsupported duration fields must reject instant-based millisecond conversion");
    } catch (UnsupportedOperationException expected) {
    }

    try {
        field.getMillis(1L, 0L);
        fail("Unsupported duration fields must reject instant-based long millisecond conversion");
    } catch (UnsupportedOperationException expected) {
    }
}

public void testUnsupportedDurationFieldRejectsLongArithmeticAndUnitMillis() {
    org.joda.time.field.UnsupportedDurationField field =
        org.joda.time.field.UnsupportedDurationField.getInstance(org.joda.time.DurationFieldType.days());

    try {
        field.add(0L, 1L);
        fail("Unsupported duration fields must reject long addition");
    } catch (UnsupportedOperationException expected) {
    }

    try {
        field.getDifferenceAsLong(1L, 0L);
        fail("Unsupported duration fields must reject long difference calculation");
    } catch (UnsupportedOperationException expected) {
    }

    try {
        field.getUnitMillis();
        fail("Unsupported duration fields must not expose a unit length");
    } catch (UnsupportedOperationException expected) {
    }
}

public void testUnsupportedDurationFieldSerializationRestoresCanonicalInstance() throws Exception {
    org.joda.time.field.UnsupportedDurationField original =
        org.joda.time.field.UnsupportedDurationField.getInstance(org.joda.time.DurationFieldType.days());

    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(original);
    output.close();

    java.io.ObjectInputStream input = new java.io.ObjectInputStream(
        new java.io.ByteArrayInputStream(bytes.toByteArray()));
    Object restored = input.readObject();
    input.close();

    assertSame(original, restored);
}

public void testPartialArrayConstructorCopiesAcceptedArrays() {
    org.joda.time.DateTimeFieldType[] types = new org.joda.time.DateTimeFieldType[] {
        org.joda.time.DateTimeFieldType.hourOfDay(),
        org.joda.time.DateTimeFieldType.minuteOfHour()
    };
    int[] values = new int[] {10, 20};

    org.joda.time.Partial partial = new org.joda.time.Partial(types, values);

    types[0] = org.joda.time.DateTimeFieldType.dayOfMonth();
    values[0] = 3;
    values[1] = 4;

    assertEquals(org.joda.time.DateTimeFieldType.hourOfDay(), partial.getFieldType(0));
    assertEquals(10, partial.getValue(0));
    assertEquals(20, partial.getValue(1));
}