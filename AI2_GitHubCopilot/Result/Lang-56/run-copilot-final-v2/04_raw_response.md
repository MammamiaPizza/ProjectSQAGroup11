@Test
public void testFormatObjectWithCalendar() {
    FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd",
java.util.TimeZone.getTimeZone("UTC"));
    java.util.Calendar cal = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
    cal.set(2023, 0, 15, 12, 0, 0);
    cal.set(java.util.Calendar.MILLISECOND, 0);
    assertEquals("2023-01-15", fdf.format(cal));
}

@Test
public void testFormatObjectWithLong() {
    FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd");
    long millis = 1673740800000L;
    assertEquals(fdf.format(new java.util.Date(millis)), fdf.format(new Long(millis)));
}

@Test
public void testFormatObjectWithUnknownTypeThrowsException() {
    FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd");
    try {
        fdf.format("not a date");
        fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        // expected
    }
}

@Test
public void testEquals() {
    FastDateFormat fdf1 = FastDateFormat.getInstance("yyyy-MM-dd",
java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.ENGLISH);
    FastDateFormat fdf2 = FastDateFormat.getInstance("yyyy-MM-dd",
java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.ENGLISH);
    assertTrue(fdf1.equals(fdf2));
    assertEquals(fdf1.hashCode(), fdf2.hashCode());
    FastDateFormat fdf3 = FastDateFormat.getInstance("yyyy-MM-dd HH",
java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.ENGLISH);
    assertFalse(fdf1.equals(fdf3));
}