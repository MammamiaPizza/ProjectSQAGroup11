@Test
public void testUnsupportedDurationFieldGetValueWithInstantShouldThrow() {
    org.joda.time.field.UnsupportedDurationField field =
org.joda.time.field.UnsupportedDurationField.getInstance(org.joda.time.DurationFieldType.eras());
    try {
        field.getValue(0L, 0L);
        fail("Expected UnsupportedOperationException");
    } catch (java.lang.UnsupportedOperationException expected) {
    }
}

@Test
public void testUnsupportedDurationFieldGetMillisLongShouldThrow() {
    org.joda.time.field.UnsupportedDurationField field =
org.joda.time.field.UnsupportedDurationField.getInstance(org.joda.time.DurationFieldType.eras());
    try {
        field.getMillis(0L);
        fail("Expected UnsupportedOperationException");
    } catch (java.lang.UnsupportedOperationException expected) {
    }
}

@Test
public void testUnsupportedDurationFieldGetMillisValueInstantShouldThrow() {
    org.joda.time.field.UnsupportedDurationField field =
org.joda.time.field.UnsupportedDurationField.getInstance(org.joda.time.DurationFieldType.eras());
    try {
        field.getMillis(0, 0L);
        fail("Expected UnsupportedOperationException");
    } catch (java.lang.UnsupportedOperationException expected) {
    }
}

@Test
public void testPartialConstructorWithTypeValueChronology() {
    org.joda.time.Partial partial = new org.joda.time.Partial(
        org.joda.time.DateTimeFieldType.year(),
        2000,
        org.joda.time.chrono.ISOChronology.getInstanceUTC()
    );
    assertNotNull(partial);
    assertEquals(1, partial.size());
}