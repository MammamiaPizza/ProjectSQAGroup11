@org.junit.Test
public void testSafeAddIntHandlesValuesAndOverflow() {
    org.junit.Assert.assertEquals(1, org.joda.time.field.FieldUtils.safeAdd(4, -3));
    org.junit.Assert.assertEquals(java.lang.Integer.MAX_VALUE,
            org.joda.time.field.FieldUtils.safeAdd(java.lang.Integer.MAX_VALUE - 1, 1));
    org.junit.Assert.assertEquals(java.lang.Integer.MIN_VALUE,
            org.joda.time.field.FieldUtils.safeAdd(java.lang.Integer.MIN_VALUE + 1, -1));

    try {
        org.joda.time.field.FieldUtils.safeAdd(java.lang.Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (java.lang.ArithmeticException expected) {
    }

    try {
        org.joda.time.field.FieldUtils.safeAdd(java.lang.Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (java.lang.ArithmeticException expected) {
    }
}

@org.junit.Test
public void testSafeAddLongHandlesValuesAndOverflow() {
    org.junit.Assert.assertEquals(1L, org.joda.time.field.FieldUtils.safeAdd(4L, -3L));
    org.junit.Assert.assertEquals(java.lang.Long.MAX_VALUE,
            org.joda.time.field.FieldUtils.safeAdd(java.lang.Long.MAX_VALUE - 1L, 1L));
    org.junit.Assert.assertEquals(java.lang.Long.MIN_VALUE,
            org.joda.time.field.FieldUtils.safeAdd(java.lang.Long.MIN_VALUE + 1L, -1L));

    try {
        org.joda.time.field.FieldUtils.safeAdd(java.lang.Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (java.lang.ArithmeticException expected) {
    }

    try {
        org.joda.time.field.FieldUtils.safeAdd(java.lang.Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (java.lang.ArithmeticException expected) {
    }
}

@org.junit.Test
public void testGetWrappedValueWrapsBothDirectionsAndRejectsInvalidRange() {
    org.junit.Assert.assertEquals(2,
            org.joda.time.field.FieldUtils.getWrappedValue(12, 1, 5));
    org.junit.Assert.assertEquals(5,
            org.joda.time.field.FieldUtils.getWrappedValue(0, 1, 5));
    org.junit.Assert.assertEquals(1,
            org.joda.time.field.FieldUtils.getWrappedValue(-4, 1, 5));

    try {
        org.joda.time.field.FieldUtils.getWrappedValue(1, 5, 1);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException expected) {
    }
}

@org.junit.Test
public void testEqualsHandlesIdentityNullAndDistinctObjects() {
    org.junit.Assert.assertTrue(org.joda.time.field.FieldUtils.equals(null, null));
    org.junit.Assert.assertTrue(org.joda.time.field.FieldUtils.equals("value", "value"));
    org.junit.Assert.assertFalse(org.joda.time.field.FieldUtils.equals(null, "value"));
    org.junit.Assert.assertFalse(org.joda.time.field.FieldUtils.equals("value", null));
    org.junit.Assert.assertFalse(org.joda.time.field.FieldUtils.equals("value", "other"));
}